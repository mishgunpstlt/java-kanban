package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter writer, LocalDateTime time) throws IOException {
        if (time == null) {
            writer.nullValue();
        } else {
            writer.value(time.format(formatter));
        }
    }

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        return LocalDateTime.parse(reader.nextString(), formatter);
    }
}
