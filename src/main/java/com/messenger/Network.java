package com.messenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Network {
    private Network() {
        throw new IllegalStateException("Utility class");
    }

    static byte getByte(DataInputStream in) throws IOException {
        return in.readByte();
    }

    static int getInt(DataInputStream in) throws IOException {
        return in.readInt();
    }

    static String getString(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] buffer = new byte[length];
        in.readFully(buffer);
        return new String(buffer);
    }

    static void sendByte(DataOutputStream out, byte b) throws IOException {
        out.writeByte(b);
    }

    static void sendInt(DataOutputStream out, int i) throws IOException {
        out.writeInt(i);
    }

    static void sendString(DataOutputStream out, String s) throws IOException {
        byte[] buffer = s.getBytes();
        out.writeInt(buffer.length);
        out.write(buffer);
    }
}
