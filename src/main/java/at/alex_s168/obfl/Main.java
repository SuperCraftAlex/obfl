package at.alex_s168.obfl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Stack;
import java.util.stream.Stream;

public class Main {

    private static String[] lines;
    private static int toDecode;
    private static Stack<Integer> stack;

    public static void main(String[] arguments) {
        Path filePath = Path.of(arguments[0]);
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {

            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            error("File not found!");
        }

        lines = contentBuilder.toString().split("\n");

        stack = new Stack<>();

        toDecode = 0;
        while (toDecode != -1) {
            if(toDecode > lines.length-1)
                System.exit(0);
            decodeLine(toDecode);
        }
    }

    private static void decodeLine(int id) {
        String line = lines[id];

        String[] t = line.split("//");
        if(t.length == 0)
            return;
        line = t[0].trim();

        int stage = 0;
        int[] spl = new int[3];

        for(char c : line.toCharArray()) {
            if(c == ' ')
                stage++;
            else if(c == 'I')
                spl[stage]++;
            else
                error("Symbol not allowed here!");
        }

        int amount = spl[0];

        for (int i = 0; i < amount; i++) {
            int oldtd = toDecode;
            runc(spl[1], spl[2]);
            if(toDecode != oldtd)
                return;
        }

        toDecode = id + 1;
    }

    private static void runc(int cmd, int arg) {
        switch (cmd) {
            case 0->{}
            case 5-> stack.push(arg); // push args onto the stack
            case 12-> stack.push(stack.pop()+stack.pop()); // adds two elements from the stack
            case 2-> stack.push(stack.pop()-stack.pop()); // subtracts two elements from the stack
            case 22-> stack.push(stack.pop()*stack.pop()); // multiplies two elements from the stack
            case 7-> stack.push(stack.pop()/stack.pop()); // divides two elements from the stack
            case 31-> stack.push((stack.pop().equals(stack.pop())) ? 1 : 0); // compares two elements from the stack
            case 8-> { if (stack.pop() == 1) toDecode = arg; } // jumps to the given code line if top stack element is 1
            case 27-> { // swaps the top elements on the stack
                int a = stack.pop();
                int b = stack.pop();
                stack.push(a);
                stack.push(b);
            }
            case 9-> stack.pop(); // just pops one elemnt from the stack
            case 33-> stack.push(stack.size()); // pushes the size of the stack onto the stack
            case 13-> stack.push(stack.peek()); // peeks the stack
            case 21-> System.out.print(stack.pop()); // prints the top element of the stack as int (and pops)
            case 35-> System.out.print((char)(int)stack.pop()); // prints the top element of the stack as ASCII char (and pops)
            case 26-> System.out.println(); // new line
            case 1-> {
                System.out.println("\n=====STACK DUMP=====");
                stack.forEach(System.out::println);
                System.out.println("====================");
            } // stack dump (debug

            default -> error("command "+encrypt(cmd)+" does not exist!");
        }
    }

    private static String encrypt(int num) {
        return "I".repeat(Math.max(0, num));
    }

    private static void error(String s) {
        System.out.println("\n"+s);
        System.exit(-1);
    }

}