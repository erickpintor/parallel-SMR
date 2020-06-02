package demo.dict;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Random;

final class Command {

    private enum Type {
        INC(true) {
            @Override
            Integer execute(Map<Integer, Integer> state, Command cmd) {
                Integer key = cmd.getKey();
                Integer next = state.get(key) + 1;
                state.put(key, next);
                return next;
            }
        },

        GET(false) {
            @Override
            Integer execute(Map<Integer, Integer> state, Command cmd) {
                return state.get(cmd.getKey());
            }
        };

        private final boolean isWrite;

        Type(boolean isWrite) {
            this.isWrite = isWrite;
        }

        abstract Integer execute(Map<Integer, Integer> state, Command cmd);
    }

    private final ByteBuffer data;

    private Command(ByteBuffer data) {
        this.data = data;
    }

    private Command(Type type, int key) {
        // [1|type][4|key]
        data = ByteBuffer.allocate(5);
        data.put((byte) type.ordinal());
        data.putInt(key);
        data.flip();
    }

    static Command wrap(byte[] data) {
        return new Command(ByteBuffer.wrap(data));
    }

    static Command random(Random random,
                          int maxKey,
                          float sparseness,
                          float conflict) {
        Type type;
        if (random.nextFloat() >= conflict) {
            type = Type.GET;
        } else {
            type = Type.INC;
        }

        int key, mid = maxKey / 2;
        do {
            key = (int) Math.round(
                    random.nextGaussian() * (mid * sparseness) + mid);
        } while (key < 0 || key >= maxKey);

        return new Command(type, key);
    }

    private Type getType() {
        data.position(0);
        return Type.values()[data.get()];
    }

    private Integer getKey() {
        data.position(1);
        return data.getInt();
    }

    boolean conflictWith(Command other) {
        return (getType().isWrite || other.getType().isWrite)
                && getKey().equals(other.getKey());
    }

    Integer execute(Map<Integer, Integer> state) {
        return getType().execute(state, this);
    }

    byte[] encode() {
        return data.array();
    }

    @Override
    public String toString() {
        return getType() + "(" + getKey() + ")";
    }
}
