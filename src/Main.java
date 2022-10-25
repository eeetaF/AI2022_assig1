import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    static int algorithm, winsA = 0, losesA = 0;
    static final int n = 9;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Initial map generation
        ArrayList<ArrayList<Cell>> map = Algorithms.generateInitialMap();

        // current map generation
        System.out.print("Choose map generation (1 - input.txt, 2 - console, 3 - test generation): ");
        char Choice = sc.next().charAt(0);
        while (Choice != '1' && Choice != '2' && Choice != '3') {
            System.out.print("Wrong choice. Choose map generation (1 - input.txt, 2 - console, 3 - test generation): ");
            Choice = sc.next().charAt(0);
        }

        int generations = 1;
        switch (Choice) {
            case '1' ->
                    // Read map from file
                    Algorithms.readMapFromFile(map);
            case '2' ->
                    // Read map from console
                    Algorithms.readMapFromConsole(map);
            case '3' -> {
                // Generate map
                System.out.print("Enter number of generations: ");
                generations = sc.nextInt();
                Algorithms.generateMap(map);
            }
        }

        if (Choice != '3') {
            // set algorithm
            System.out.print("Choose algorithm (1 - A*, 2 - Backtracking): ");
            char charAlgorithm = sc.next().charAt(0);
            while (charAlgorithm != '1' && charAlgorithm != '2') {
                System.out.print("Wrong choice. Choose algorithm (1 - A*, 2 - Backtracking): ");
                charAlgorithm = sc.next().charAt(0);
            }
            algorithm = charAlgorithm - '0';

            int result;
            Algorithms.printMap(map);

            long start = System.nanoTime();
            if (algorithm == 1) {
                result = A_star.start(map);
                long finish = System.nanoTime();
                long timeElapsed = finish - start;
                if (result != -1) {
                    try {
                        FileWriter myWriter = new FileWriter("outputAStar.txt", true);
                        myWriter.write(timeElapsed / 1000000 + " ms\n");
                        myWriter.close();
                    } catch (Exception ignored) {}
                }
                System.out.println("Time elapsed: " + timeElapsed / 1000000 + " ms");
            } else {
                result = Backtracking.start(map);
                long finish = System.nanoTime();
                long timeElapsed = finish - start;
                if (result != -1) {
                    try {
                        FileWriter myWriter = new FileWriter("outputBacktracking.txt", true);
                        myWriter.write(timeElapsed / 1000000 + " ms\n");
                        myWriter.close();
                    } catch (Exception ignored) {}
                }
                System.out.println("Time elapsed: " + timeElapsed / 1000000 + " ms");
            }
            System.out.println(result);
            Algorithms.clearMap(map);
            Algorithms.generateMap(map);
        } else {
            int resultA, resultB;
            long begin = System.nanoTime();
            for (int i = 0; i < generations; i++) {
                Algorithms.printMap(map);

                System.out.println("Generation #" + (i + 1));
                System.out.println("Time from beginning: " + (System.nanoTime() - begin) / 1000000000 + " sec");

                System.out.println("A*");
                long start = System.nanoTime();
                resultA = A_star.start(map);
                long finish = System.nanoTime();
                long timeElapsed = finish - start;
                System.out.println("Time elapsed: " + timeElapsed / 1000000 + " ms");
                System.out.println("Result: " + resultA);
                System.out.println();

                System.out.println("Backtracking");
                start = System.nanoTime();
                resultB = Backtracking.start(map);
                finish = System.nanoTime();
                timeElapsed = finish - start;
                System.out.println("Time elapsed: " + timeElapsed / 1000000 + " ms");
                System.out.println("Result: " + resultB);
                System.out.println();
                if (resultA != resultB) {
                    break;
                }

                Algorithms.clearMap(map);
                Algorithms.generateMap(map);
            }
        }
        sc.close();
    }

    public enum Cell {
        // Empty, Jack, Davy, Kraken, Rock, Tortuga, Chest, dangerZone
        E, J, D, K, R, T, C, Z, JZ, KR
        // JZ - Special case, Jack and Danger cell
        // KR - Special case, Kraken and Rock cell
    }

    public static class Algorithms {
        public static ArrayList<ArrayList<Cell>> generateInitialMap() {
            ArrayList<ArrayList<Cell>> map = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                map.add(new ArrayList<>());
                for (int j = 0; j < n; j++)
                    map.get(i).add(Cell.E);

            }
            return map;
        }

        public static void clearMap(ArrayList<ArrayList<Cell>> map) {
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    map.get(i).set(j, Cell.E);
        }

        public static void printMap(ArrayList<ArrayList<Cell>> map) {
            for (int i = 0; i < n * 2 + 1; i++)
                System.out.print("-");
            System.out.print("\n ");
            for (int i = 0; i < n; i++) {
                System.out.print(" " + i);
            }
            System.out.println();

            for (int i = 0; i < n; i++) {
                System.out.print(i);
                for (int j = 0; j < n; j++) {
                    switch (map.get(i).get(j)) {
                        case E -> System.out.print(" -");
                        case J -> System.out.print(" *");
                        case D -> System.out.print(" D");
                        case K -> System.out.print(" K");
                        case R -> System.out.print(" R");
                        case T -> System.out.print(" T");
                        case C -> System.out.print(" C");
                        case Z -> System.out.print(" Z");
                        case JZ -> System.out.print("JZ");
                        case KR -> System.out.print("KR");
                    }
                }
                System.out.println();
            }
            for (int i = 0; i < n * 2 + 1; i++)
                System.out.print("-");
            System.out.println();
        }

        public static void readMapFromFile(ArrayList<ArrayList<Cell>> map) {
            try {
                Scanner sc = new Scanner(new File("input.txt"));
                readMap(map, sc);
            } catch (Exception e) {
                System.out.println("Input file is invalid or does not exist!");}


        }

        public static void readMap(ArrayList<ArrayList<Cell>> map, Scanner sc) throws Exception {
            for (int i = 0; i < 6; i++) {
                String coordinates = sc.next();
                int x = coordinates.charAt(1) - '0';
                int y = coordinates.charAt(3) - '0';
                switch (i) {
                    case 0 -> {
                        if (x != 0 || y != 0) throw new Exception();
                        map.get(x).set(y, Cell.J);
                    }
                    case 1 -> {
                        if (x == 0 && y == 0) throw new Exception();
                        map.get(x).set(y, Cell.D);
                        Algorithms.FillDangerDavy(map, x, y);
                    }
                    case 2 -> {
                        if ((x == 0 && y == 0) || (map.get(x).get(y) == Cell.D)) throw new Exception();
                        map.get(x).set(y, Cell.K);
                        Algorithms.FillDangerKraken(map, x, y);
                    }
                    case 3 -> {
                        if (map.get(x).get(y) == Cell.E || map.get(x).get(y) == Cell.Z)
                            map.get(x).set(y, Cell.R);
                        else if (map.get(x).get(y) == Cell.K)
                            map.get(x).set(y, Cell.KR);
                        else
                            throw new Exception();
                    }
                    case 4 -> {
                        if ((x == 0 && y == 0) || (map.get(x).get(y) == Cell.D) || (map.get(x).get(y) == Cell.K)
                                || (map.get(x).get(y) == Cell.Z) || (map.get(x).get(y) == Cell.R) || (map.get(x).get(y) == Cell.KR))
                            throw new Exception();
                        map.get(x).set(y, Cell.C);
                    }
                    case 5 -> {
                        if ((x == 0 && y == 0) || (map.get(x).get(y) == Cell.D) || (map.get(x).get(y) == Cell.K)
                                || (map.get(x).get(y) == Cell.Z) || (map.get(x).get(y) == Cell.R) || (map.get(x).get(y) == Cell.KR)
                                || (map.get(x).get(y) == Cell.C))
                            throw new Exception();
                        map.get(x).set(y, Cell.T);
                    }
                }
            }
            sc.close();
        }

        public static void readMapFromConsole(ArrayList<ArrayList<Cell>> map) {
            try {
                Scanner sc = new Scanner(System.in);
                readMap(map, sc);
            } catch (Exception e) {
                System.out.println("Input is invalid, try again!");
                Algorithms.clearMap(map);
                Algorithms.readMapFromConsole(map);
            }
        }

        public static void generateMap(ArrayList<ArrayList<Cell>> map) {
            // Jack
            map.get(0).set(0, Cell.J);
            Random random = new Random();

            // Davy
            int xD, yD;
            do {
                xD = random.nextInt(n);
                yD = random.nextInt(n);
            } while (xD == 0 && yD == 0);
            map.get(xD).set(yD, Cell.D);
            Algorithms.FillDangerDavy(map, xD, yD);

            // Kraken
            int xK, yK;
            do {
                xK = random.nextInt(n);
                yK = random.nextInt(n);
            } while ((xD == xK && yD == yK) || (xK == 0 && yK == 0));
            map.get(xK).set(yK, Cell.K);
            Algorithms.FillDangerKraken(map, xK, yK);

            // Chest
            int xC, yC;
            do {
                xC = random.nextInt(n);
                yC = random.nextInt(n);
            } while ((xD == xC && yD == yC) || (xK == xC && yK == yC) || (xC == 0 && yC == 0) ||
                    (map.get(xC).get(yC) == Cell.Z));
            map.get(xC).set(yC, Cell.C);

            // Tortuga
            int xT, yT;
            do {
                xT = random.nextInt(n);
                yT = random.nextInt(n);
            } while ((xD == xT && yD == yT) || (xK == xT && yK == yT) || (xC == xT && yC == yT) || (xT == 0 && yT == 0)
                    || (map.get(xT).get(yT) == Cell.Z));
            map.get(xT).set(yT, Cell.T);

            // Rock
            int xR, yR;
            do {
                xR = random.nextInt(n);
                yR = random.nextInt(n);
            } while ((xD == xR && yD == yR) || (xC == xR && yC == yR) || (xT == xR && yT == yR)
                    || (xR == 0 && yR == 0));
            if (map.get(xR).get(yR) == Cell.K) {
                map.get(xR).set(yR, Cell.KR);
            } else {
                map.get(xR).set(yR, Cell.R);
            }
        }

        public static void FillDangerDavy(ArrayList<ArrayList<Cell>> map, int x, int y) {
            if (x + 1 < n) map.get(x + 1).set(y, Cell.Z);
            if (x - 1 >= 0) map.get(x - 1).set(y, Cell.Z);
            if (y + 1 < n) map.get(x).set(y + 1, Cell.Z);
            if (y - 1 >= 0) map.get(x).set(y - 1, Cell.Z);
            if (x + 1 < n && y + 1 < n) map.get(x + 1).set(y + 1, Cell.Z);
            if (x + 1 < n && y - 1 >= 0) map.get(x + 1).set(y - 1, Cell.Z);
            if (x - 1 >= 0 && y + 1 < n) map.get(x - 1).set(y + 1, Cell.Z);
            if (x - 1 >= 0 && y - 1 >= 0) map.get(x - 1).set(y - 1, Cell.Z);
            if (map.get(0).get(0) == Cell.Z) map.get(0).set(0, Cell.JZ);
        }

        public static void FillDangerDavyM(ArrayList<ArrayList<A_star_node>> map, int x, int y) {
            if (x + 1 < n) map.get(x + 1).get(y).cell = Cell.Z;
            if (x - 1 >= 0) map.get(x - 1).get(y).cell = Cell.Z;
            if (y + 1 < n) map.get(x).get(y + 1).cell = Cell.Z;
            if (y - 1 >= 0) map.get(x).get(y - 1).cell = Cell.Z;
            if (x + 1 < n && y + 1 < n) map.get(x + 1).get(y + 1).cell = Cell.Z;
            if (x + 1 < n && y - 1 >= 0) map.get(x + 1).get(y - 1).cell = Cell.Z;
            if (x - 1 >= 0 && y + 1 < n) map.get(x - 1).get(y + 1).cell = Cell.Z;
            if (x - 1 >= 0 && y - 1 >= 0) map.get(x - 1).get(y - 1).cell = Cell.Z;
        }

        public static void FillDangerKraken(ArrayList<ArrayList<Cell>> map, int x, int y) {
            if (x + 1 < n && map.get(x + 1).get(y) != Cell.D) map.get(x + 1).set(y, Cell.Z);
            if (x - 1 >= 0 && map.get(x - 1).get(y) != Cell.D) map.get(x - 1).set(y, Cell.Z);
            if (y + 1 < n && map.get(x).get(y + 1) != Cell.D) map.get(x).set(y + 1, Cell.Z);
            if (y - 1 >= 0 && map.get(x).get(y - 1) != Cell.D) map.get(x).set(y - 1, Cell.Z);
            if (map.get(0).get(0) == Cell.Z) map.get(0).set(0, Cell.JZ);
        }

        public static class PairedArrayList {
            public ArrayList<Integer> xCells, yCells;

            public PairedArrayList() {
                xCells = new ArrayList<>();
                yCells = new ArrayList<>();
            }

            public PairedArrayList(PairedArrayList other) {
                xCells = new ArrayList<>(other.xCells);
                yCells = new ArrayList<>(other.yCells);
            }

            public boolean contains(int x, int y) {
                for (int i = 0; i < xCells.size(); i++) {
                    if (xCells.get(i) == x && yCells.get(i) == y) return true;
                }
                return false;
            }

            public void add(int x, int y) {
                xCells.add(x);
                yCells.add(y);
            }

            public int size() {
                return xCells.size();
            }

            public void remove(int i) {
                if (i < xCells.size()) {
                    xCells.remove(i);
                    yCells.remove(i);
                }
            }

            public void switchValues(int i, int j) {
                if (i < xCells.size() && j < xCells.size()) {
                    int x = xCells.get(i);
                    int y = yCells.get(i);
                    xCells.set(i, xCells.get(j));
                    yCells.set(i, yCells.get(j));
                    xCells.set(j, x);
                    yCells.set(j, y);
                }
            }
        }
    }

    public static class A_star {
        // way 1 - Jack -> Chest
        // way 2 - Jack -> Tortuga -> (Kraken) -> Chest
        static int shortest1, shortest2;
        static A_star_node jack, chest, tortuga, davy;
        static ArrayList<A_star_node> sortedList;
        static ArrayList<A_star_node> foundPath2, foundPath1;
        static ArrayList<ArrayList<A_star_node>> nodeMap;

        // returns an integer - minimum number of moves to get to the chest (-1 if impossible)
        public static int start(ArrayList<ArrayList<Cell>> map) {
            if (map.get(0).get(0) == Cell.JZ) return -1;

            nodeMap = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                nodeMap.add(new ArrayList<>());
                for (int j = 0; j < n; j++) {
                    nodeMap.get(i).add(new A_star_node(i, j, map.get(i).get(j)));
                    if (map.get(i).get(j) == Cell.J) {
                        jack = nodeMap.get(i).get(j);
                    }
                    if (map.get(i).get(j) == Cell.T) {
                        tortuga = nodeMap.get(i).get(j);
                    }
                    if (map.get(i).get(j) == Cell.C) {
                        chest = nodeMap.get(i).get(j);
                    }
                    if (map.get(i).get(j) == Cell.D) {
                        davy = nodeMap.get(i).get(j);
                    }
                }
            }

            findShortest1();

            foundPath2 = new ArrayList<>();
            shortest2 = 0;

            boolean isTortuga = findShortest2(jack, tortuga);
            if (shortest2 != -1 && isTortuga) findShortest2(tortuga, chest);

            System.out.println("Shortest way 1: " + shortest1 + " Shortest way 2: " + shortest2);

            if (shortest1 == -1 || shortest2 == -1) {
                if (shortest2 == -1)
                    parsePath(foundPath1);
                else
                    parsePath(foundPath2);
                return Math.max(shortest1, shortest2);
            }
            if (shortest1 < shortest2) {
                parsePath(foundPath1);
                return shortest1;
            } else {
                parsePath(foundPath2);
                return shortest2;
            }
        }

        public static void findShortest1() {
            sortedList = new ArrayList<>();
            // set p of every node
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    nodeMap.get(i).get(j).p = Math.max(Math.abs(nodeMap.get(i).get(j).x - chest.x),
                            Math.abs(nodeMap.get(i).get(j).y - chest.y));
                }
            }
            nodeMap.get(0).get(0).set_a(0);
            sortedList.add(nodeMap.get(0).get(0));
            try {
                while (true) {
                    if (sortedList.get(0).sum > 999) {
                        shortest1 = -1;
                        return;
                    }
                    A_star_node currentNode = sortedList.get(0);
                    sortedList.remove(0);
                    A_star_node ChestNode = inspectArea(currentNode, nodeMap, false);
                    if (ChestNode != null) {
                        shortest1 = ChestNode.a;
                        break;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                shortest1 = -1;
            }
            if (foundPath2 != null)
                foundPath1 = foundPath2;
        }

        public static boolean findShortest2(A_star_node start, A_star_node end) {
            int a = 0, x = 0, y = 0;
            boolean flag = false;
            if (start != jack) {
                a = nodeMap.get(start.x).get(start.y).a;
                x = start.x;
                y = start.y;
                flag = true;
            }
            resetNodeMap();
            sortedList = new ArrayList<>();
            if (flag) {
                nodeMap.get(x).get(y).tortugaPassed = true;
            }
            // set p of every node
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    nodeMap.get(i).get(j).p = Math.max(Math.abs(start.x - end.x), Math.abs(start.y - end.y));
                }
            }
            nodeMap.get(x).get(y).set_a(a);
            sortedList.add(nodeMap.get(x).get(y));
            try {
                while (true) {
                    if (sortedList.get(0).sum > 999) {
                        shortest2 = -1;
                        return false;
                    }
                    A_star_node currentNode = sortedList.get(0);
                    sortedList.remove(0);
                    if (currentNode.tortugaPassed)
                        findKrakenInArea(currentNode);
                    A_star_node ChestNode = inspectArea(currentNode, nodeMap, !flag);
                    if (ChestNode != null) {
                        shortest2 = ChestNode.a;
                        return ChestNode.cell != Cell.C;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                shortest2 = -1;
                return false;
            }
        }

        public static A_star_node inspectArea(A_star_node currentNode, ArrayList<ArrayList<A_star_node>> nodeMap, boolean flag) {
            if (currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x).get(currentNode.y - 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x).get(currentNode.y + 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y + 1 < n && currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y + 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y + 1 < n && currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y + 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y - 1 >= 0 && currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y - 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            if (currentNode.y - 1 >= 0 && currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y - 1);
                if (DefineConnectionType(currentNode, radNode, flag)) return radNode;
            }
            return null;
        }

        public static void findKrakenInArea(A_star_node currentNode) {
            if (currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x).get(currentNode.y - 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x).get(currentNode.y + 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y + 1 < n && currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y + 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y + 1 < n && currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y + 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y - 1 >= 0 && currentNode.x - 1 >= 0) {
                A_star_node radNode = nodeMap.get(currentNode.x - 1).get(currentNode.y - 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                    return;
                }
            }
            if (currentNode.y - 1 >= 0 && currentNode.x + 1 < n) {
                A_star_node radNode = nodeMap.get(currentNode.x + 1).get(currentNode.y - 1);
                if (radNode.cell == Cell.K || radNode.cell == Cell.KR) {
                    killKraken(radNode);
                }
            }
        }

        private static boolean DefineConnectionType(A_star_node currentNode, A_star_node radNode, boolean flag) {
            Cell radCell;
            radCell = radNode.cell;
            if (radCell == Cell.C || (radCell == Cell.T && flag)) {
                radNode.connect_good_nodes(currentNode);
                findPath(radNode);
                return true;
            }
            if (radCell == Cell.Z || radCell == Cell.D || radCell == Cell.R || radCell == Cell.K || radCell == Cell.KR) {
                radNode.connect_bad_nodes(currentNode);
                return false;
            }
            if (radCell == Cell.T) {
                radNode.tortugaPassed = true;
            }
            if (radNode.connect_good_nodes(currentNode)) {
                sortedList.add(radNode);
                sortedList.sort(Comparator.comparingInt(o -> o.sum));
            }
            return false;
        }

        public static void findPath(A_star_node node) {
            A_star_node currentNode = node;
            ArrayList<A_star_node> temp = new ArrayList<>();
            if (foundPath2 == null)
                foundPath2 = new ArrayList<>();
            while (true) {
                temp.add(currentNode);
                if (currentNode.parents.size() == 0) {
                    break;
                }
                currentNode = currentNode.parents.get(0);
            }
            Collections.reverse(temp);
            foundPath2.addAll(temp);
        }

        public static void killKraken(A_star_node kraken) {
            if (kraken.cell == Cell.KR)
                kraken.cell = Cell.R;
            else kraken.cell = Cell.E;

            if ((kraken.x + 1 < n) && (nodeMap.get(kraken.x + 1).get(kraken.y).cell == Cell.Z)) {
                nodeMap.get(kraken.x + 1).get(kraken.y).cell = Cell.E;
            }
            if ((kraken.x - 1 >= 0) && (nodeMap.get(kraken.x - 1).get(kraken.y).cell == Cell.Z)) {
                nodeMap.get(kraken.x - 1).get(kraken.y).cell = Cell.E;
            }
            if ((kraken.y - 1 >= 0) && (nodeMap.get(kraken.x).get(kraken.y - 1).cell == Cell.Z)) {
                nodeMap.get(kraken.x).get(kraken.y - 1).cell = Cell.E;
            }
            if ((kraken.y + 1 < n) && (nodeMap.get(kraken.x).get(kraken.y + 1).cell == Cell.Z)) {
                nodeMap.get(kraken.x).get(kraken.y + 1).cell = Cell.E;
            }
            Algorithms.FillDangerDavyM(nodeMap, davy.x, davy.y);
        }

        public static void resetNodeMap() {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    nodeMap.get(i).set(j, new A_star_node(i, j, nodeMap.get(i).get(j).cell));
                }
            }
        }

        public static void parsePath(ArrayList<A_star_node> path) {
            Algorithms.PairedArrayList parsedPath = new Algorithms.PairedArrayList();
            if (path != null)
                for (A_star_node a_star_node : path)
                    if (!parsedPath.contains(a_star_node.x, a_star_node.y))
                        parsedPath.add(a_star_node.x, a_star_node.y);

            printPath(parsedPath);
        }

        public static void printPath(Algorithms.PairedArrayList path) {
            try {
                FileWriter myWriter = new FileWriter("outputAStar.txt");
                Backtracking.WritePath(path, myWriter);
            } catch (Exception ignored) {}
        }
    }

    public static class A_star_node {
        public int a, p, sum, x, y;
        // Actual cost, Potential cost, Sum = a + p, (x,y) - coordinates
        public Cell cell;
        public boolean tortugaPassed;
        public ArrayList<A_star_node> parents;

        public A_star_node(int x, int y, Cell cell) {
            parents = new ArrayList<>();
            a = -1;
            p = 1000;
            sum = 1000;
            this.x = x;
            this.y = y;
            this.cell = cell;
            tortugaPassed = false;
        }

        public void set_a(int a) {
            this.a = a;
            this.sum = a + p;
        }

        public boolean connect_good_nodes(A_star_node parent_node) {
            if (parents.contains(parent_node))
                return false;
            if (this.sum > parent_node.a + 1 + this.p) {
                this.parents.clear();
                this.parents.add(parent_node);
                if (parent_node.tortugaPassed)
                    this.tortugaPassed = true;
                this.set_a(parent_node.a + 1);

                return true;
            } else if (this.sum == parent_node.a + 1 + this.p && (!this.tortugaPassed || parent_node.tortugaPassed)) {
                this.set_a(parent_node.a + 1);
                this.parents.add(parent_node);
                if (parent_node.tortugaPassed)
                    this.tortugaPassed = true;
                return true;
            }
            return false;
        }

        public void connect_bad_nodes(A_star_node parent_node) {
            this.parents.clear();
            this.parents.add(parent_node);
            this.set_a(1000);
        }
    }

    public static class Backtracking {
        // way 1 - Jack -> Chest
        // way 2 - Jack -> Tortuga -> (Kraken) -> Chest
        static int shortest2t, shortest1, shortest2, xtortuga, ytortuga, xchest, ychest, maxSteps, xdavy, ydavy, xkraken, ykraken;
        static long iterations, maxiterations1 = Integer.MAX_VALUE, maxiterations2 = Integer.MAX_VALUE;
        static Algorithms.PairedArrayList foundPath1, foundPath2, tempFoundPath2;
        static ArrayList<ArrayList<Cell>> noKrakenMap, mapWithKraken;

        public static int start(ArrayList<ArrayList<Cell>> map) {
            shortest1 = -1;
            shortest2 = -1;
            mapWithKraken = map;

            findShortest1();
            findShortest2();

            System.out.println("Shortest way 1: " + shortest1 + " Shortest way 2: " + shortest2);

            if (shortest1 == -1 || shortest2 == -1) {
                if (shortest1 == -1)
                    printPath(foundPath2);
                else
                    printPath(foundPath1);
                return Math.max(shortest1, shortest2);
            }

            if (shortest1 <= shortest2) {
                printPath(foundPath1);
                return shortest1;
            } else {
                printPath(foundPath2);
                return shortest2;
            }
        }

        public static void findShortest1() {
            if (mapWithKraken.get(0).get(0) == Cell.JZ) {
                shortest1 = -1;
                return;
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (mapWithKraken.get(i).get(j) == Cell.C) {
                        xchest = i;
                        ychest = j;
                    }
                    if (mapWithKraken.get(i).get(j) == Cell.T) {
                        xtortuga = i;
                        ytortuga = j;
                    }
                    if (mapWithKraken.get(i).get(j) == Cell.D) {
                        xdavy = i;
                        ydavy = j;
                    }
                    if (mapWithKraken.get(i).get(j) == Cell.K || mapWithKraken.get(i).get(j) == Cell.KR) {
                        xkraken = i;
                        ykraken = j;
                    }
                }
            }

            maxSteps = n * 3 + n / 3;
            iterations = 0;
            findRec1(0, 0, 0, new Algorithms.PairedArrayList());
        }

        public static void findRec1(int x, int y, int nOfSteps, Algorithms.PairedArrayList visitedCells) {
            iterations++;
            if (nOfSteps >= maxSteps || x < 0 || y < 0 || x >= n || y >= n || mapWithKraken.get(x).get(y) == Cell.Z ||
                    mapWithKraken.get(x).get(y) == Cell.R || mapWithKraken.get(x).get(y) == Cell.KR ||
                    mapWithKraken.get(x).get(y) == Cell.K || mapWithKraken.get(x).get(y) == Cell.D ||
                    visitedCells.contains(x, y) || iterations > maxiterations1) {
                return;
            }

            Algorithms.PairedArrayList visitedCellsCopy = new Algorithms.PairedArrayList(visitedCells);
            visitedCellsCopy.add(x, y);
            if (x == xchest && y == ychest) {
                if (shortest1 < 0 || nOfSteps < shortest1) {
                    shortest1 = nOfSteps;
                    foundPath1 = visitedCellsCopy;
                    maxSteps = nOfSteps;
                    return;
                }
            }

            Algorithms.PairedArrayList optimized = AreaOptimization(x, y, xchest, ychest);

            for (int i = 0; i < optimized.size(); i++)
                findRec1(optimized.xCells.get(i), optimized.yCells.get(i), nOfSteps + 1, visitedCellsCopy);
        }

        public static Algorithms.PairedArrayList AreaOptimization(int x, int y, int xDestination, int yDestination) {
            int k1 = Math.abs((x - 1) - xDestination) + Math.abs((y - 1) - yDestination);
            int k2 = Math.abs((x - 1) - xDestination) + Math.abs(y - yDestination);
            int k3 = Math.abs(x - xDestination) + Math.abs((y - 1) - yDestination);
            int k4 = Math.abs((x + 1) - xDestination) + Math.abs((y + 1) - yDestination);
            int k5 = Math.abs((x + 1) - xDestination) + Math.abs(y - yDestination);
            int k6 = Math.abs(x - xDestination) + Math.abs((y + 1) - yDestination);
            int k7 = Math.abs((x - 1) - xDestination) + Math.abs((y + 1) - yDestination);
            int k8 = Math.abs((x + 1) - xDestination) + Math.abs((y - 1) - yDestination);

            ArrayList<Integer> list = new ArrayList<>();
            list.add(k1);
            list.add(k2);
            list.add(k3);
            list.add(k4);
            list.add(k5);
            list.add(k6);
            list.add(k7);
            list.add(k8);

            Algorithms.PairedArrayList optimized = new Algorithms.PairedArrayList();
            optimized.add(x - 1, y - 1);
            optimized.add(x - 1, y);
            optimized.add(x, y - 1);
            optimized.add(x + 1, y + 1);
            optimized.add(x + 1, y);
            optimized.add(x, y + 1);
            optimized.add(x - 1, y + 1);
            optimized.add(x + 1, y - 1);

            int N = 8;
            int temp;
            for (int i = 0; i < N; i++) {
                for (int j = 1; j < (N - i); j++) {
                    if (list.get(j - 1) > list.get(j)) {
                        temp = list.get(j - 1);
                        list.set(j - 1, list.get(j));
                        list.set(j, temp);
                        optimized.switchValues(j - 1, j);
                    }
                }
            }
            return optimized;
        }

        public static void findShortest2() {
            if (mapWithKraken.get(0).get(0) == Cell.JZ) {
                shortest1 = -1;
                return;
            }

            foundPath2 = new Algorithms.PairedArrayList();
            tempFoundPath2 = new Algorithms.PairedArrayList();
            noKrakenMap = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                noKrakenMap.add(new ArrayList<>());
                for (int j = 0; j < n; j++) {
                    noKrakenMap.get(i).add(mapWithKraken.get(i).get(j));
                }
            }
            killKraken(noKrakenMap, xkraken, ykraken);
            Algorithms.FillDangerDavy(noKrakenMap, xdavy, ydavy);

            maxSteps = shortest1 > 0 ? shortest1 : n * 3 + n / 3;

            shortest2t = -1;
            iterations = 0;
            findRec2(0, 0, 0, new Algorithms.PairedArrayList(), false, false);
            if (shortest2t < 0) return;

            shortest2 = shortest2t;

            maxSteps = shortest1 > 0 ? shortest1 : n * 3 + n / 3;

            iterations = 0;
            shortest2t = -1;
            foundPath2.remove(foundPath2.size() - 1);
            findRec2(xtortuga, ytortuga, 0, new Algorithms.PairedArrayList(), true, false);
            if (shortest2t < 0) {
                shortest2 = -1;
                return;
            }
            for (int i = 0; i < tempFoundPath2.size(); i++) {
                foundPath2.add(tempFoundPath2.xCells.get(i), tempFoundPath2.yCells.get(i));
            }

            shortest2 += shortest2t;
        }

        public static void findRec2(int x, int y, int nOfSteps, Algorithms.PairedArrayList visitedCells,
                                    boolean tortugaPassed, boolean krakenKilled) {
            iterations++;
            ArrayList<ArrayList<Cell>> map = krakenKilled ? noKrakenMap : mapWithKraken;


            if (nOfSteps >= maxSteps || x < 0 || y < 0 || x >= n || y >= n || map.get(x).get(y) == Cell.Z ||
                    map.get(x).get(y) == Cell.R || map.get(x).get(y) == Cell.D || visitedCells.contains(x, y) ||
                    iterations > maxiterations2 ||
                    ((map.get(x).get(y) == Cell.KR || map.get(x).get(y) == Cell.K) && !tortugaPassed)
            ) {
                return;
            }

            if (tortugaPassed && !krakenKilled)
                krakenKilled = killKrakenInArea(x, y);

            map = krakenKilled ? noKrakenMap : mapWithKraken;

            if ((map.get(x).get(y) == Cell.KR || map.get(x).get(y) == Cell.K) && tortugaPassed) {
                map = noKrakenMap;
                krakenKilled = true;
                if (map.get(x).get(y) == Cell.R || map.get(x).get(y) == Cell.Z) return;
            }

            Algorithms.PairedArrayList visitedCellsCopy = new Algorithms.PairedArrayList(visitedCells);
            visitedCellsCopy.add(x, y);
            if (x == xchest && y == ychest && tortugaPassed) {
                if (shortest2t < 0 || nOfSteps < shortest2t) {
                    shortest2t = nOfSteps;
                    tempFoundPath2 = visitedCellsCopy;
                    maxSteps = nOfSteps;
                    return;
                }
            }

            if (x == xtortuga && y == ytortuga && !tortugaPassed) {
                if (shortest2t < 0 || nOfSteps < shortest2t) {
                    shortest2t = nOfSteps;
                    foundPath2 = visitedCellsCopy;
                    maxSteps = nOfSteps;
                    return;
                }
            }

            Algorithms.PairedArrayList optimized;
            if (tortugaPassed)
                 optimized = AreaOptimization(x, y, xchest, ychest);
            else
                optimized = AreaOptimization(x, y, xtortuga, ytortuga);

            for (int i = 0; i < optimized.size(); i++) {
                findRec2(optimized.xCells.get(i), optimized.yCells.get(i), nOfSteps + 1,
                        visitedCellsCopy, tortugaPassed, krakenKilled);
            }
        }

        public static void killKraken(ArrayList<ArrayList<Cell>> map, int xKraken, int yKraken) {
            if (map.get(xKraken).get(yKraken) == Cell.KR)
                map.get(xKraken).set(yKraken, Cell.R);
            else map.get(xKraken).set(yKraken, Cell.E);

            if ((xKraken + 1 < n) && (map.get(xKraken + 1).get(yKraken) == Cell.Z)) {
                map.get(xKraken + 1).set(yKraken, Cell.E);
            }
            if ((xKraken - 1 >= 0) && (map.get(xKraken - 1).get(yKraken) == Cell.Z)) {
                map.get(xKraken - 1).set(yKraken, Cell.E);
            }
            if ((yKraken + 1 < n) && (map.get(xKraken).get(yKraken + 1) == Cell.Z)) {
                map.get(xKraken).set(yKraken + 1, Cell.E);
            }
            if ((yKraken - 1 >= 0) && (map.get(xKraken).get(yKraken - 1) == Cell.Z)) {
                map.get(xKraken).set(yKraken - 1, Cell.E);
            }
            Algorithms.FillDangerDavy(map, xdavy, ydavy);
        }

        public static boolean killKrakenInArea(int x, int y) {
            if (x - 1 > 0 && y - 1 > 0 &&
                    (mapWithKraken.get(x - 1).get(y - 1) == Cell.K || mapWithKraken.get(x - 1).get(y - 1) == Cell.KR)) {
                return true;
            }
            if (x - 1 > 0 && y + 1 < n &&
                    (mapWithKraken.get(x - 1).get(y + 1) == Cell.K || mapWithKraken.get(x - 1).get(y + 1) == Cell.KR)) {
                return true;
            }
            if (x + 1 < n && y - 1 > 0 &&
                    (mapWithKraken.get(x + 1).get(y - 1) == Cell.K || mapWithKraken.get(x + 1).get(y - 1) == Cell.KR)) {
                return true;
            }
            if (x + 1 < n && y + 1 < n &&
                    (mapWithKraken.get(x + 1).get(y + 1) == Cell.K || mapWithKraken.get(x + 1).get(y + 1) == Cell.KR)) {
                return true;
            }
            return false;
        }

        public static void printPath(Algorithms.PairedArrayList path) {
            try {
                FileWriter myWriter = new FileWriter("outputBacktracking.txt");
                WritePath(path, myWriter);
            } catch (Exception ignored) {
            }
        }

        public static void WritePath(Algorithms.PairedArrayList path, FileWriter myWriter) throws IOException {
            if (path.size() != 0) {
                myWriter.write("Win\n" + (path.size() - 1) + "\n");
                for (int i = 0; i < path.size(); i++) {
                    myWriter.write("[" + path.xCells.get(i) + "," + path.yCells.get(i) + "] ");
                }
                myWriter.write("\n");
                for (int i = 0; i < n * 2 + 1; i++) {
                    myWriter.write("-");
                }
                myWriter.write("\n ");
                for (int i = 0; i < n; i++) {
                    myWriter.write(" " + i);
                }
                myWriter.write("\n");
                for (int i = 0; i < n; i++) {
                    myWriter.write(i + " ");
                    for (int j = 0; j < n; j++) {
                        if (path.contains(i, j)) {
                            myWriter.write("* ");
                        } else {
                            myWriter.write("- ");
                        }
                    }
                    myWriter.write("\n");
                }
                for (int i = 0; i < n * 2 + 1; i++) {
                    myWriter.write("-");
                }
            } else {
                myWriter.write("Lose");
            }
            myWriter.write("\n");
            myWriter.close();
        }
    }
}
