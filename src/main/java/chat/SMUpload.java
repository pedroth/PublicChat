package chat;


import utils.State;
import utils.SuffixTreeTokenizer;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

class SMUpload {
    private String fileName;
    private LinkedList<Integer> data;
    private State<Integer> state;
    private int index = 0;

    private State<Integer> stateFive = new State<Integer>() {
        @Override
        public State<Integer> next(Integer x) {
            return stateFive;
        }
    };

    private State<Integer> stateFour = new State<Integer>() {
        final String uploadHeader = "------WebKitFormBoundary";
        private SuffixTreeTokenizer tokenizer;

        {
            tokenizer = new SuffixTreeTokenizer(new String[]{uploadHeader});
            tokenizer.init();
        }

        @Override
        public State<Integer> next(Integer x) {
            final Optional<String> token = tokenizer.next((char) x.intValue());
            if (token.isPresent()) {
                index = index - uploadHeader.length() + 1;
                IntStream.range(0, uploadHeader.length() + 1).forEach(v -> data.pollLast());
                return stateFive;
            }
            data.add(x);
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
            return tokenizer.next((char) x.intValue())
                    .map(v -> stateTwo)
                    .orElse(stateOne);
        }
    };

    SMUpload() {
        this.data = new LinkedList<>();
        this.state = this.stateOne;
    }

    String getFileName() {
        return fileName;
    }

    List<Integer> getData() {
        return this.data;
    }

    void next(int c) {
        state = state.next(c);
    }
}