package chat;


import utils.State;
import utils.SuffixTreeTokenizer;

import java.util.Optional;

class SMUpload {
    private String fileName;
    private byte[] buffer;
    private State<Integer> state;
    private int index = 0;

    private State<Integer> stateFive = new State<Integer>() {
        @Override
        public State<Integer> next(Integer x) {
            return stateFive;
        }
    };

    private State<Integer> stateFour = new State<Integer>() {
        final String regex = "------WebKitFormBoundary";
        private SuffixTreeTokenizer tokenizer;

        {
            tokenizer = new SuffixTreeTokenizer(new String[]{regex});
            tokenizer.init();
        }

        @Override
        public State<Integer> next(Integer x) {
            final Optional<String> token = tokenizer.next((char) x.intValue());
            if (index < buffer.length) {
                buffer[index] = x.byteValue();
            }
            if (token.isPresent()) {
                index = index - regex.length() + 1;
                return stateFive;
            }
            index++;
            return stateFour;
        }
    };

    private State<Integer> stateThree = new State<Integer>() {
        private int accNewLines = 0;

        @Override
        public State<Integer> next(Integer x) {
            if (x == '\n') {
                accNewLines++;
            }
            if (accNewLines == 3) {
                accNewLines = 0;
                return stateFour;
            }
            return stateThree;
        }
    };

    private State<Integer> stateTwo = new State<Integer>() {
        private StringBuilder stack = new StringBuilder();

        @Override
        public State<Integer> next(Integer x) {
            if (x == '"') {
                fileName = this.stack.toString();
                this.stack = new StringBuilder();
                return stateThree;
            }
            this.stack.append((char) x.intValue());
            return stateTwo;
        }
    };

    private State<Integer> stateOne = new State<Integer>() {
        private SuffixTreeTokenizer tokenizer;

        {
            tokenizer = new SuffixTreeTokenizer(new String[]{"filename=\""});
            tokenizer.init();
        }

        @Override
        public State<Integer> next(Integer x) {
            final Optional<String> next = tokenizer.next((char) x.intValue());
            if (next.isPresent()) {
                return stateTwo;
            }
            return stateOne;
        }
    };

    SMUpload(byte[] buffer) {
        this.buffer = buffer;
        this.state = this.stateOne;
    }

    String getFileName() {
        return fileName;
    }

    int getIndex() {
        return index;
    }

    void next(int c) {
        state = state.next(c);
    }
}