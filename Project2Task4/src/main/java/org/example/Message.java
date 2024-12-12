/**
 * Author: Jike Lu
 * ID: jikelu
 */
package org.example;

public class Message {
    String request, response;
    double val1, val2, val3, val4;
    int iterations;
    String status;

    public Message() {
        this.request = "UNKNOWN";
        response = this.request;
        this.val1 = 0.0;
        this.val2 = 0.0;
        this.val3 = 0.0;
        this.val4 = 0.0;
        this.iterations = 0;
        status = "NOT OK";
    }
    public Message(String request) {
        this.request = request;
        response = this.request;
        this.val1 = 0.0;
        this.val2 = 0.0;
        this.val3 = 0.0;
        this.val4 = 0.0;
        this.iterations = 0;
        status = "OK";
    }

    public Message(String request, double val1, double val2, double val3, double val4) {
        this.request = request;
        response = this.request;
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
        this.val4 = val4;
        this.iterations = 0;
        status = "OK";
    }

    public Message(String request, double val1, double val2) {
        this.request = request;
        response = this.request;
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = 0.0;
        this.val4 = 0.0;
        this.iterations = 0;
        status = "OK";
    }

    public Message(String request, double val1) {
        this.request = request;
        response = this.request;
        this.val1 = val1;
        this.val2 = 0.0;
        this.val3 = 0.0;
        this.val4 = 0.0;
        this.iterations = 0;
        status = "OK";
    }

    public Message(String request, int iterations) {
        this.request = request;
        response = this.request;
        this.val1 = 0.0;
        this.val2 = 0.0;
        this.val3 = 0.0;
        this.val4 = 0.0;
        this.iterations = iterations;
        status = "OK";
    }

    public String toString(String request, boolean response) {
        if (!response) {
            switch (request) {
                case "getCurrentRange":
                    return "{'request':'" + this.request + "'}";
                case "setCurrentRange":
                    return "{'request':'" + this.request + "','val1':" + val1 + ",'val2':" + val2 + ",'val3':" + val3 +",'val4':" + val4 +"}";
                case "train":
                    return "{'request':'" + this.request + "','iterations':" + iterations + "}";
                case "test":
                    return "{'request':'" + this.request + "','val1':" + val1 + ",'val2':" + val2 +"}";
            }
            return "{'request':" + this.request + "'}";
        }
        else {
            switch (request) {
                case "getCurrentRange":
                    return "{'response':'" + this.response + "','status':'" + this.status + "','val1':" + val1 + ",'val2':" + val2 + ",'val3':" + val3 +",'val4':" + val4 +"}";
                case "setCurrentRange":
                    return "{'response':'" + this.response + "','status':'" + this.status + "'}";
                case "train":
                    return "{'response':'" + this.response + "','status':'" + this.status + "','val1':" + val1 + "}";
                case "test":
                    return "{'response':'" + this.response + "','status':'" + this.status + "','val1':" + val1 + "}";
            }
            return "{'response':'" + this.response + "','status':'" + this.status + "'}";
        }
    }
}
