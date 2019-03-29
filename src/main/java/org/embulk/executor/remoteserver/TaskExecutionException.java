package org.embulk.executor.remoteserver;

class TaskExecutionException extends RuntimeException {
    TaskExecutionException(String message) {
        super(message);
    }

    TaskExecutionException(Throwable cause) {
        super(cause);
    }
}
