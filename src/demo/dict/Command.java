package demo.dict;

import java.nio.ByteBuffer;
import java.util.Map;

final class Command {

    private enum Type {
        PUT {
            @Override
            Integer execute(Map<Integer, Integer> state, Encoded cmd) {
                return state.put(cmd.getKey(), cmd.getValue());
            }
        },

        GET {
            @Override
            Integer execute(Map<Integer, Integer> state, Encoded cmd) {
                return state.get(cmd.getKey());
            }
        };

        abstract Integer execute(Map<Integer, Integer> state, Encoded command);
    }

    private final Type type;
    private final int key;
    private final Integer value;

    private Command(Type type, int key, Integer value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    static Command put(int key, int value) {
        return new Command(Type.PUT, key, value);
    }

    static Command get(int key) {
        return new Command(Type.GET, key, null);
    }

    static final class Encoded {

        private final ByteBuffer data;

        private Encoded(ByteBuffer data) {
            this.data = data;
        }

        private Type getType() {
            data.position(0);
            return Type.values()[data.get()];
        }

        private Integer getKey() {
            data.position(1);
            return data.getInt();
        }

        private Integer getValue() {
            data.position(5);
            return data.getInt();
        }

        boolean conflictWith(Encoded other) {
            if (getType() == Type.PUT || other.getType() == Type.PUT)
                return getKey().equals(other.getKey());
            return false;
        }

        Integer execute(Map<Integer, Integer> state) {
            return getType().execute(state, this);
        }
    }

    static Encoded wrap(byte[] data) {
        return new Encoded(ByteBuffer.wrap(data));
    }

    static Encoded encode(Command command) {
        // [1|type][4|key][4|value]
        ByteBuffer data = ByteBuffer.allocate(9);
        data.put((byte) command.type.ordinal());
        data.putInt(command.key);
        data.putInt(command.value);
        data.flip();
        return new Encoded(data);
    }
}
