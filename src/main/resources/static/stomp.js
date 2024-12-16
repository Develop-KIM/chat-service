// STOMP 클라이언트 객체 생성, WebSocket 브로커 URL 설정
const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/stomp/chats' // WebSocket 브로커의 URL
});

// STOMP 연결 성공 시 호출되는 콜백 함수
stompClient.onConnect = (frame) => {
  setConnected(true); // 연결 상태를 UI에 반영
  console.log('Connected: ' + frame); // 연결 성공 메시지 출력

  // 특정 경로("/sub/chats")를 구독하여 메시지를 수신
  stompClient.subscribe('/sub/chats', (chatMessage) => {
    // 수신된 메시지를 화면에 표시
    showMessage(JSON.parse(chatMessage.body)); // 메시지 본문을 JSON으로 파싱하여 처리
  });

  // 연결 후 특정 경로("/pub/chats")로 메시지를 발행하여 연결 메시지를 알림
  stompClient.publish({
    destination: "/pub/chats", // 메시지 발행 경로
    body: JSON.stringify({ // 발행할 메시지의 내용
      'message': "connected" // 연결 상태를 알리는 메시지
    })
  });
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

  // 연결 상태에 따라 대화창 표시 여부 결정
  if (connected) {
    $("#conversation").show(); // 연결 시 대화창 표시
  } else {
    $("#conversation").hide(); // 해제 시 대화창 숨김
  }

  // 메시지 영역 초기화
  $("#messages").html("");
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
  stompClient.publish({
    destination: "/pub/chats", // 메시지를 발행할 경로
    body: JSON.stringify({ // 발행할 메시지의 내용
      'message': $("#message").val() // 입력한 메시지
    })
  });
  // 메시지 입력 필드 초기화
  $("#message").val("")
}

// 수신된 메시지를 대화창에 표시하는 함수
function showMessage(chatMessage) {
  // 메시지를 테이블의 새로운 행으로 추가
  $("#messages").append(
      "<tr><td>" + chatMessage.sender + " : " + chatMessage.message
      + "</td></tr>");
}

// 페이지 로드 시 이벤트 핸들러 등록
$(function () {
  // 폼 제출 이벤트를 막아 새로고침 방지
  $("form").on('submit', (e) => e.preventDefault());

  // 연결 버튼 클릭 시 연결 함수 호출
  $("#connect").click(() => connect());

  // 해제 버튼 클릭 시 연결 해제 함수 호출
  $("#disconnect").click(() => disconnect());

  // 전송 버튼 클릭 시 메시지 전송 함수 호출
  $("#send").click(() => sendMessage());
});
