package InvokerServer;

import io.restassured.response.Response;

public class TestExpection extends Exception {

  private Response serverResponse;


  public TestExpection(String message, Response aResponse) {
    super(message);
    this.serverResponse = aResponse;
  }

  public Response getServerResponse() {
    return this.serverResponse;
  }

}
