package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Copied from PedroEngine and Learning repo to remove dependencies
 */
public class SuffixTreeTokenizer extends TokenRecognizer {
    protected Node root;
    private Node iterState;

    public SuffixTreeTokenizer(String[] patterns) {
        super(patterns);
        root = new Node("root");
    }

    public void init() {
        String[] p = this.getPatterns();
        for (String s : p) ConstructDSM(s, root);
        ConstructFailFunction();
    }

    private void ConstructDSM(String pattern, Node n) {
        int patternSize = pattern.length();
        for (int i = 0; i < patternSize; i++) {
            Character c = pattern.charAt(i);
            if (n.get(c) == null) {
                n.put(c);
            }
            n = n.get(c);
        }
        n.setFinalState(true);
        n.setToken(pattern);
    }

    private void ConstructFailFunction() {
        Set<Character> keys = root.map.keySet();
        for (Character k : keys) {
            Node n = root.get(k);
            buildFailFunction(n);
        }
    }

    private void buildFailFunction(Node n) {
        String[] p = this.getPatterns();
        Node oldState = n;
        StringBuilder stack = new StringBuilder();
        for (String s : p) {
            int strSize = s.length();
            for (int k = 0; k < strSize; k++) {
                Character aux = s.charAt(k);
                stack.append(aux);
                if (n.get(aux) != null) {
                    n = n.get(aux);
                } else {
                    n.putFail(aux, searchNode(stack.toString()));
                    stack = new StringBuilder();
                    n = oldState;
                    break;
                }
            }
        }
    }

    private Node searchNode(String s) {
        int size = s.length();
        Node n = root;
        for (int i = 0; i < size; i++) {
            n = n.get(s.charAt(i));
        }
        return n;
    }

    @Override
    public String[] tokenize(String s) {
        Node state = root;
        int textSize = s.length();
        List<String> answer = new ArrayList<>();

        for (int i = 0; i < textSize; i++) {
            Character c = s.charAt(i);
            if (state.hasNext(c)) {
                state = state.get(c);
            } else if (state.hasNextFail(c)) {
                state = state.getFail(c);
            } else if (state.isFinalState()) {
                answer.add(state.getToken());
                i--;
                state = root;
            } else {
                state = root;
            }
        }
        if (state.isFinalState()) answer.add(state.getToken());
        return answer.toArray(new String[0]);
    }

    public Optional<String> next(char c) {
        if (iterState == null) {
            iterState = root;
        }
        if (iterState.hasNext(c)) {
            iterState = iterState.get(c);
        } else if (iterState.hasNextFail(c)) {
            iterState = iterState.getFail(c);
        } else {
            iterState = root;
        }
        if (iterState.isFinalState()) {
            final String token = iterState.getToken();
            iterState = root;
            return Optional.of(token);
        }
        return Optional.empty();
    }

    public void printStateMachine(Node n) {
        HashSet<Node> visited = new HashSet<>();
        Stack<Integer> level = new Stack<>();
        Stack<Node> stack = new Stack<>();
        stack.push(n);
        level.push(0);
        while (!stack.empty()) {
            Node node = stack.pop();
            visited.add(node);
            int pop = level.pop();
            if (node.getName() != null && "root".equals(node.getName())) {
                System.out.println("transition function:");
            }
            Set<Character> keys = node.map.keySet();
            Set<Character> failKeys = node.failFunction.keySet();
            Set<Character> unionSet = new HashSet<>(keys.size() + failKeys.size());
            unionSet.addAll(keys);
            unionSet.addAll(failKeys);
            for (Character k : unionSet) {
                for (int i = 0; i < pop; i++) {
                    System.out.print(" ");
                }
                Node next = null;
                if (failKeys.contains(k)) {
                    System.out.println(node.getName() + " ~--> " + k);
                    final Node fail = node.getFail(k);
                    next = visited.contains(fail) ? null : fail;
                } else {
                    System.out.println(node.getName() + " --> " + k);
                    final Node item = node.get(k);
                    next = visited.contains(item) ? null : item;
                }
                if (next != null) {
                    stack.push(next);
                    level.push(pop + 1);
                }
            }
        }
    }

    class Node {
        private Map<Character, Node> map;
        private Map<Character, Node> failFunction;
        private boolean finalState;
        private String token;
        private String name;

        Node(String name) {
            map = new HashMap<Character, Node>();
            failFunction = new HashMap<Character, Node>();
            this.name = name;
            finalState = false;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public boolean isFinalState() {
            return finalState;
        }

        public void setFinalState(boolean finalState) {
            this.finalState = finalState;
        }

        public void put(Character c) {
            map.put(c, new Node("" + c));
        }

        public Node get(Character c) {
            return map.get(c);
        }

        public boolean hasNext(Character c) {
            return this.get(c) != null;
        }

        public void putFail(Character c, Node n) {
            failFunction.put(c, n);
        }

        public Node getFail(Character c) {
            return failFunction.get(c);
        }

        public boolean hasNextFail(Character c) {
            return failFunction.get(c) != null;
        }
    }


    public static void main(String args[]) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String[] s = {"x", "exp", "gauss", "euler", "pedro", "sin", "cos", "ln", "sinh", "cosh", "tanh", "acos", "asin", "acosh", "asinh", "she", "hers"};
        SuffixTreeTokenizer st = new SuffixTreeTokenizer(s);
        st.init();
        st.printStateMachine(st.root);
        try {
            String line = in.readLine();
            String[] tokens = st.tokenize(line);
            for (String token : tokens) {
                System.out.println(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
