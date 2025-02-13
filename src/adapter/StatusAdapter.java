package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.Status;

import java.io.IOException;

public class StatusAdapter extends TypeAdapter<Status> {

    @Override
    public void write(JsonWriter writer, Status status) throws IOException {
        writer.value(status.name());
    }

    @Override
    public Status read(JsonReader reader) throws IOException {
        return Status.valueOf(reader.nextString().toUpperCase());
    }
}
