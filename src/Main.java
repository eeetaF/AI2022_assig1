import java.io.File;
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

//        // set scenario
//        System.out.print("Choose scenario (1, 2): ");
//        Choice = sc.next().charAt(0);
//        while (Choice != '1' && Choice != '2') {
//            System.out.print("Wrong choice. Choose scenario (1, 2): ");
//            Choice = sc.next().charAt(0);
//        }
//        scenario = Choice - '0';

        // set algorithm
        System.out.print("Choose algorithm (1 - A*, 2 - Backtracking): ");
        Choice = sc.next().charAt(0);
        while (Choice != '1' && Choice != '2') {
            System.out.print("Wrong choice. Choose algorithm (1 - A*, 2 - Backtracking): ");
            Choice = sc.next().charAt(0);
        }
        algorithm = Choice - '0';

        int result;
        if (algorithm == 1) {
            for (int i = 0; i < generations; i++) {
                Algorithms.printMap(map);
                result = A_star.start(map);
                System.out.println(result);
                Algorithms.clearMap(map);
                Algorithms.generateMap(map);
            }
        }

        /* output example

         Win
         9
         [0,0] [1,1] [1,2] [2,3] [3,4] [4,5] [5,5] [6,6] [7,7] [8,7]
         -------------------
         0 1 2 3 4 5 6 7 8
         0 * - - - - - - - -
         1 - * * - - - - - -
         2 - - - * - - - - -
         3 - - - - * - - - -
         4 - - - - - * - - -
         5 - - - - - * - - -
         6 - - - - - - * - -
         7 - - - - - - - * -
         8 - - - - - - - * -
         -------------------
         100 ms */
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
            ArrayList<ArrayList<Cell>> map = new ArrayList<ArrayList<Cell>>();
            for (int i = 0; i < n; i++) {
                map.add(new ArrayList<Cell>());
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
            } catch (Exception e) {
                System.out.println("Input file is invalid or does not exist!");
            }


        }

        public static void readMapFromConsole(ArrayList<ArrayList<Cell>> map) {
            try {
                Scanner sc = new Scanner(System.in);
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
    }

    public static class A_star {
        // way 1 - Jack -> Chest
        // way 2 - Jack -> Tortuga -> (Kraken) -> Chest
        static int shortest1, shortest2;
        static A_star_node jack, chest, tortuga, davy;
        static ArrayList<A_star_node> sortedList;
        static ArrayList<A_star_node> foundPath;
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

            shortest2 = 0;
            findShortest2(jack, tortuga);
            if (shortest2 != -1) findShortest2(tortuga, chest);

            System.out.println(shortest1 + " " + shortest2);
            if (shortest1 == -1 || shortest2 == -1) return Math.max(shortest1, shortest2);
            return Math.min(shortest1, shortest2);
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
        }

        public static void findShortest2(A_star_node start, A_star_node end) {
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
                        return;
                    }
                    A_star_node currentNode = sortedList.get(0);
                    sortedList.remove(0);
                    A_star_node ChestNode = inspectArea(currentNode, nodeMap, !flag);
                    if (ChestNode != null) {
                        shortest2 = ChestNode.a;
                        break;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                shortest2 = -1;
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

        private static boolean DefineConnectionType(A_star_node currentNode, A_star_node radNode, boolean flag) {
            Cell radCell;
            radCell = radNode.cell;
            if (radCell == Cell.C || (radCell == Cell.T && flag)) {
                radNode.connect_good_nodes(currentNode);
                foundPath = findPath(radNode);
                return true;
            }
            if (radCell == Cell.Z || radCell == Cell.D || radCell == Cell.R) {
                radNode.connect_bad_nodes(currentNode);
                return false;
            }
            if (radCell == Cell.K || radCell == Cell.KR) {
                if (currentNode.tortugaPassed) {
                    if (radCell == Cell.KR)
                        radNode.connect_bad_nodes(currentNode);
                    else
                        radNode.connect_good_nodes(currentNode);
                    killKraken(radNode);
                } else {
                    radNode.connect_bad_nodes(currentNode);
                    return false;
                }
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

        public static ArrayList<A_star_node> findPath(A_star_node node) {
            ArrayList<A_star_node> path = new ArrayList<>();
            A_star_node currentNode = node;
            while (currentNode != null) {
                path.add(currentNode);
                currentNode = currentNode.get_parent();
            }
            return path;
        }

        public static void killKraken(A_star_node kraken) {
            if (kraken.cell == Cell.KR)
                kraken.cell = Cell.R;
            else kraken.cell = Cell.E;

            if ((kraken.x + 1 < n) && (nodeMap.get(kraken.x + 1).get(kraken.y).cell == Cell.Z)) {
                nodeMap.get(kraken.x + 1).get(kraken.y).cell = Cell.E;
                nodeMap.get(kraken.x + 1).get(kraken.y).connect_good_nodes(kraken);
            }
            if ((kraken.x - 1 >= 0) && (nodeMap.get(kraken.x - 1).get(kraken.y).cell == Cell.Z)) {
                nodeMap.get(kraken.x - 1).get(kraken.y).cell = Cell.E;
                nodeMap.get(kraken.x - 1).get(kraken.y).connect_good_nodes(kraken);
            }
            if ((kraken.y - 1 >= 0) && (nodeMap.get(kraken.x).get(kraken.y - 1).cell == Cell.Z)) {
                nodeMap.get(kraken.x).get(kraken.y - 1).cell = Cell.E;
                nodeMap.get(kraken.x).get(kraken.y - 1).connect_good_nodes(kraken);
            }
            if ((kraken.y + 1 < n) && (nodeMap.get(kraken.x).get(kraken.y + 1).cell == Cell.Z)) {
                nodeMap.get(kraken.x).get(kraken.y + 1).cell = Cell.E;
                nodeMap.get(kraken.x).get(kraken.y + 1).connect_good_nodes(kraken);
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
            if (this.sum > parent_node.a + 1 + this.p) {
                this.parents.clear();
                this.parents.add(parent_node);;
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

        public A_star_node get_parent() {
            if (this.parents.size() == 0) return null;
            return this.parents.get(0);
        }
    }
}
