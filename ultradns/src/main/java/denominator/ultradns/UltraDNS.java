package denominator.ultradns;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import feign.Headers;
import feign.RequestLine;
import java.io.IOException;

@Headers("Content-Type: application/json")
interface UltraDNS {

  //@Body("https://restapi.ultradns.com/v1/status")




  @RequestLine("GET /status")
  NetworkStatus getNeustarNetworkStatus();


  enum NetworkStatus {
    GOOD, FAILED;
  }

  static class NetworkStatusAdapter extends TypeAdapter<NetworkStatus> {

    @Override
    public void write(JsonWriter out, NetworkStatus value) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public NetworkStatus read(JsonReader in) throws IOException {
      NetworkStatus status = NetworkStatus.FAILED;
      in.beginObject();
      if (in.hasNext() && in.nextName().equals("message") &&
          in.nextString().equalsIgnoreCase("Good")) {
        status = NetworkStatus.GOOD;
      }
      in.endObject();
      return status;
    }
  }
}

