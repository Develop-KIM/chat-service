// STOMP 클라이언트 객체 생성, WebSocket 브로커 URL 설정
const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/stomp/chats' // WebSocket 브로커의 URL
});

// STOMP 연결 성공 시 호출되는 콜백 함수
stompClient.onConnect = (frame) => {
  setConnected(true); // 연결 상태를 UI에 반영
  showChatrooms();
  console.log('Connected: ' + frame); // 연결 성공 메시지 출력
};

// WebSocket 연결 에러 발생 시 호출되는 콜백 함수
stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error); // WebSocket 에러를 콘솔에 출력
};

// STOMP 프로토콜 오류 발생 시 호출되는 콜백 함수
stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']); // 에러 메시지 출력
  console.error('Additional details: ' + frame.body); // 추가적인 오류 세부 정보 출력
};

// 연결 상태를 UI에 반영하는 함수
function setConnected(connected) {
  // 연결/해제 버튼 활성화 상태 변경
  $("#connect").prop("disabled", connected); // 연결 버튼 비활성화
  $("#disconnect").prop("disabled", !connected); // 해제 버튼 활성화
  $("#create").prop("disabled", !connected);
}

// STOMP 연결을 활성화하는 함수
function connect() {
  stompClient.activate(); // STOMP 클라이언트 활성화
}

// STOMP 연결을 비활성화하는 함수
function disconnect() {
  stompClient.deactivate(); // STOMP 클라이언트 비활성화
  setConnected(false); // 연결 상태를 해제 상태로 UI 반영
  console.log("Disconnected"); // 연결 해제 로그 출력
}

// 메시지를 발행하는 함수
function sendMessage() {
  let chatroomId = $("#chatroom-id").val();
  stompClient.publish({
    destination: "/pub/chats/" + chatroomId, // 메시지를 발행할 경로
    body: JSON.stringify({ // 발행할 메시지의 내용
      'message': $("#message").val() // 입력한 메시지
    })
  });
  // 메시지 입력 필드 초기화
  $("#message").val("")
}

function createChatroom() {
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: '/chats?title=' + $("#chatroom-title").val(),
    success: function (data) {
      console.log('data: ', data);
      showChatrooms();
      enterChatroom(data.id, true);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    }
  })
}

function showChatrooms() {
    $.ajax({
      type: 'GET',
      dataType: 'json',
      url: '/chats',
      success: function (data) {
        console.log('data: ', data);
        renderChatrooms(data);
      },
      error: function (request, status, error) {
        console.log('request: ', request);
        console.log('error: ', error);
      }
    })
}

function renderChatrooms(chatrooms) {
    $("#chatroom-list").html("");
    for (let i = 0; i < chatrooms.length; i++) {
      $("#chatroom-list").append(
          "<tr onclick='joinChatroom(" + chatrooms[i].id + ")'><td>"
          + chatrooms[i].id + "</td><td>" + chatrooms[i].title + "</td><td>"
          + chatrooms[i].memberCount + "</td><td>" + chatrooms[i].createdAt
          + "</td></tr>"
      );
    }
}

let subscription;

function enterChatroom(chatroomId, newMember) {
  $("#chatroom-id").val(chatroomId);
  $("#messages").html("");
  showMessages(chatroomId);
  $("#conversation").show();
  $("#send").prop("disabled", false);
  $("#leave").prop("disabled", false);

  if (subscription != undefined) {
    subscription.unsubscribe();
  }

  subscription = stompClient.subscribe('/sub/chats/' + chatroomId,
      (chatMessage) => {
        // 수신된 메시지를 화면에 표시
        showMessage(JSON.parse(chatMessage.body)); // 메시지 본문을 JSON으로 파싱하여 처리
      });

  if (newMember) {
    // 연결 후 특정 경로("/pub/chats")로 메시지를 발행하여 연결 메시지를 알림
    stompClient.publish({
      destination: "/pub/chats/" + chatroomId, // 메시지 발행 경로
      body: JSON.stringify({
        'message': "님이 방에 입장하였습니다."
      })
    })
  }
}

function showMessages(chatroomId) {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: '/chats/' + chatroomId + '/messages',
    success: function (data) {
      console.log('data: ', data);
      for (let i = 0; i < data.length; i++) {
        showMessage(data[i]);
      }
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    }
  })
}

// 수신된 메시지를 대화창에 표시하는 함수
function showMessage(chatMessage) {
  // 메시지를 테이블의 새로운 행으로 추가
  $("#messages").append(
      "<tr><td>" + chatMessage.sender + " : " + chatMessage.message
      + "</td></tr>");
}

function joinChatroom(chatroomId) {
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: '/chats/' + chatroomId,
    success: function (data) {
      console.log('data: ', data);
      enterChatroom(chatroomId, data);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    }
  })
}

function leaveChatroom() {
  let chatroomId = $("#chatroom-id").val();
  $.ajax({
    type: 'DELETE',
    dataType: 'json',
    url: '/chats/' + chatroomId,
    success: function (data) {
      console.log('data: ', data);
      showChatrooms();
      exitChatroom(chatroomId);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    }
  })
}

function exitChatroom(chatroomId) {
  $("#chatroom-id").val("");
  $("#conversation").hide();
  $("#send").prop("disabled", true);
  $("#leave").prop("disabled", true);
}

// 페이지 로드 시 이벤트 핸들러 등록
$(function () {
  // 폼 제출 이벤트를 막아 새로고침 방지
  $("form").on('submit', (e) => e.preventDefault());

  // 연결 버튼 클릭 시 연결 함수 호출
  $("#connect").click(() => connect());

  // 해제 버튼 클릭 시 연결 해제 함수 호출
  $("#disconnect").click(() => disconnect());

  $("#create").click(() => createChatroom());
  $("#leave").click(() => leaveChatroom());
  // 전송 버튼 클릭 시 메시지 전송 함수 호출
  $("#send").click(() => sendMessage());
});

