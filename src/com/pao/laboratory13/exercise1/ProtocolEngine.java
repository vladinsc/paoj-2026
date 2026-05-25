package com.pao.laboratory13.exercise1;

public class ProtocolEngine {
    private State state = State.INIT;
    private String currentUser = null;
    private int historyCount = 0;

    public String processCommand(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.trim().split("\\s+");
        String command = parts[0];

        switch (command) {
            case "AUTH":
                return handleAuth(parts);
            case "OPEN":
                return handleOpen(parts);
            case "SEND":
                return handleSend(parts);
            case "BROADCAST":
                return handleBroadcast(parts);
            case "HISTORY":
                return handleHistory(parts);
            case "CLOSE":
                return handleClose(parts);
            default:
                return "ERR E_PARSE UNKNOWN_COMMAND";
        }
    }

    private String handleAuth(String[] parts) {
        if (parts.length < 2) {
            return "ERR E_PARSE AUTH";
        }
        if (state == State.CLOSED) {
            return "ERR E_STATE CLOSED";
        }
        currentUser = parts[1];
        state = State.AUTH;
        historyCount = 0;
        return "OK AUTH user=" + currentUser;
    }

    private String handleOpen(String[] parts) {
        if (parts.length > 1) {
            return "ERR E_PARSE OPEN";
        }
        if (state == State.CLOSED) {
            return "ERR E_STATE CLOSED";
        }
        if (state == State.OPEN) {
            return "ERR E_STATE ALREADY_OPEN";
        }
        if (state == State.INIT) {
            return "ERR E_STATE NOT_OPEN";
        }
        state = State.OPEN;
        return "OK OPEN";
    }

    private String handleSend(String[] parts) {
        if (parts.length < 2) {
            return "ERR E_PARSE SEND";
        }
        if (state == State.CLOSED) {
            return "ERR E_STATE CLOSED";
        }
        if (state != State.OPEN) {
            return "ERR E_STATE NOT_OPEN";
        }
        historyCount++;
        return "OK OPEN sent";
    }

    private String handleBroadcast(String[] parts) {
        if (parts.length < 2) {
            return "ERR E_PARSE BROADCAST";
        }
        if (state == State.CLOSED) {
            return "ERR E_STATE CLOSED";
        }
        if (state != State.OPEN) {
            return "ERR E_STATE NOT_OPEN";
        }
        historyCount++;
        return "OK OPEN broadcast";
    }

    private String handleHistory(String[] parts) {
        if (parts.length > 1) {
            return "ERR E_PARSE HISTORY";
        }
        if (state == State.CLOSED) {
            return "ERR E_STATE CLOSED";
        }
        if (state != State.OPEN) {
            return "ERR E_STATE NOT_OPEN";
        }
        return "OK OPEN history=" + historyCount;
    }

    private String handleClose(String[] parts) {
        if (parts.length > 1) {
            return "ERR E_PARSE CLOSE";
        }
        if (state == State.CLOSED) {
            return "ERR E_STATE CLOSED";
        }
        if (state != State.OPEN) {
            return "ERR E_STATE NOT_OPEN";
        }
        state = State.CLOSED;
        return "OK CLOSED";
    }

    public State getState() {
        return state;
    }
}
