package game.players;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RunningStar extends Player {

    @Override
    public Point takeTurn(String genome, Map<Point, Integer> vision) {
        Map<Point, Integer> squareCosts = decode(genome, vision);
        Path path = astar(squareCosts);
        return path.get(1);
    }

    private Path astar(Map<Point, Integer> squareCosts) {
        Set<Path> closed = new HashSet<>();
        PriorityQueue<Path> open = new PriorityQueue<>();
        open.add(new Path(new Point(0, 0), 0));
        while (!open.isEmpty()){
            Path best = open.remove();
            if (best.head().x == 2 || (best.head().x > 0 && (best.head().y == 2 || best.head().y == -2))){
                return best;
            }
            pathsAround(best, squareCosts).stream().filter(path -> !closed.contains(path) && !open.contains(path))
                    .forEach(open::add);
            closed.add(best);
        }
        Path p = new Path(new Point(0, 0), 0);
        return p.add(new Point((int) (random.nextDouble() * 3 - 1), (int) (random.nextDouble() * 3 - 1)), 0);
    }

    private List<Path> pathsAround(Path path, Map<Point, Integer> costs) {
        Point head = path.head();
        List<Path> results = new ArrayList<>();
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                if (i == 0 && j == 0){
                    continue;
                }
                Point p = new Point(head.x + i, head.y + j);
                if (!costs.containsKey(p) || costs.get(p) == -1){
                    continue;
                }
                results.add(path.add(p, costs.get(p)));
            }
        }
        return results;
    }

    private Map<Point, Integer> decode(String genome, Map<Point, Integer> vision) {
        int chunkLength = genome.length()/16;
        Map<Integer, Integer> costs = new HashMap<>();
        Map<Integer, Integer> traps = new HashMap<>();
        for (int i = 0; i < 16; i++){
            int startChunk = i * chunkLength;
            boolean isBad;
            boolean isTrap;
            isTrap = genome.substring(startChunk, startChunk + 2).equals("01");
            isBad = !isTrap && genome.charAt(startChunk) == '0';
            if (isTrap){
                int direction = 0;
                for (int j = startChunk + 2; j < startChunk + chunkLength - 1; j++){
                    int bit = Integer.parseInt(genome.charAt(j) + "");
                    direction *= 2;
                    direction += bit;
                }
                traps.put(i, direction);
                costs.put(i, genome.charAt(startChunk + chunkLength - 1) == '1' ? 1 : 0);
            } else if (isBad){
                costs.put(i, -1);
            } else {
                int cost = 0;
                int runSize = 0;
                for (int j = startChunk + 1; j < startChunk + chunkLength; j++){
                    switch (genome.charAt(j)){
                        case '0':
                            runSize = 0;
                            break;
                        case '1':
                            cost += ++runSize;
                    }
                }
                costs.put(i, cost);
            }
        }
        Map<Point, Integer> squareCosts = new HashMap<>();
        for (Map.Entry<Point, Integer> entry : vision.entrySet()){
            if (squareCosts.containsKey(entry.getKey())){
                continue;
            }
            if (entry.getValue() == -1){
                squareCosts.put(entry.getKey(), -1);
                continue;
            }
            if (costs.get(entry.getValue()) == -1){
                squareCosts.put(entry.getKey(), -1);
                continue;
            }
            if (traps.containsKey(entry.getValue())){
                Point trap;
                switch (traps.get(entry.getValue())){
                    case 0:
                        trap = new Point(-1, -1);
                        break;
                    case 1:
                        trap = new Point(-1, 0);
                        break;
                    case 2:
                        trap = new Point(-1, 1);
                        break;
                    case 3:
                        trap = new Point(0, -1);
                        break;
                    case 4:
                        trap = new Point(0, 1);
                        break;
                    case 5:
                        trap = new Point(1, -1);
                        break;
                    case 6:
                        trap = new Point(1, 0);
                        break;
                    case 7:
                        trap = new Point(1, 1);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                trap = new Point(trap.x + entry.getKey().x, trap.y + entry.getKey().y);
                squareCosts.put(trap, -1);
            }
            squareCosts.put(entry.getKey(), costs.get(entry.getValue()));
        }
        return squareCosts;
    }

    private class Path implements Comparable<Path>{

        Point head;
        Path parent;
        int length;
        int totalCost;

        private Path(){}

        public Path(Point point, int cost) {
            length = 1;
            totalCost = cost;
            head = point;
            parent = null;
        }

        public Point get(int index) {
            if (index >= length || index < 0){
                throw new IllegalArgumentException(index + "");
            }
            if (index == length - 1){
                return head;
            }
            return parent.get(index);
        }

        public Point head() {
            return head;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Path path = (Path) o;

            return head.equals(path.head);

        }

        @Override
        public int hashCode() {
            return head.hashCode();
        }

        @Override
        public int compareTo(Path o) {
            return totalCost - o.totalCost;

        }

        public Path add(Point point, int cost) {
            Path p = new Path();
            p.head = point;
            p.totalCost = totalCost + cost;
            p.length = length + 1;
            p.parent = this;
            return p;
        }
    }
}