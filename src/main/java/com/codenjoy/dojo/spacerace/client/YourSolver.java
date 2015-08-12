package com.codenjoy.dojo.spacerace.client;

import com.codenjoy.dojo.client.Direction;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.spacerace.model.Elements;

import java.util.List;

/**
 * User: your name
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
public class YourSolver implements Solver<Board> {

    private static final String USER_NAME = "kudriavtsev.oleksii@gmail.com";

    private Dice dice;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        if (board.isGameOver())
            return "";
        String result = "";
        result = findDirection(board);
        if (result != null) {
            return result;
        }
        return Direction.STOP.toString();
    }

    private String findDirection(Board board) {
        Direction result = Direction.STOP;
        Point me = new PointImpl(0,0);

        try {me = board.getMe(); }
        catch(Throwable t){        }

        if ((me != null) && (me.getX() != 0)) {
            int x = me.getX();
            int y = me.getY();
            result = findDirectionToBulletPack(board, me, result);
        }
        return CheckResult(result, board);
    }

    private Direction findDirectionToBulletPack(Board board, Point me, Direction result) {
        List<Point> boxes = board.get(Elements.BULLET_PACK);
        if (boxes.size() != 0) {
            Point box = boxes.get(0);
            if (box != null) {
                Point newMe;
                double newDistance = (double) Integer.MAX_VALUE;
                double distance;
                newMe = new PointImpl(me.getX() + 1, me.getY());

                distance = newMe.distance(box);
                if (distance < newDistance) {
                    newDistance = distance;
                    result = Direction.RIGHT;
                }

                newMe = new PointImpl(me.getX(), me.getY() + 1);
                distance = newMe.distance(box);
                if (distance < newDistance) {
                    newDistance = distance;
                    result = Direction.DOWN;
                }

                newMe = new PointImpl(me.getX() - 1, me.getY());
                distance = newMe.distance(box);
                if (distance < newDistance) {
                    newDistance = distance;
                    result = Direction.LEFT;
                }

                newMe = new PointImpl(me.getX(), me.getY() - 1);
                distance = newMe.distance(box);
                if (distance < newDistance) {
                    newDistance = distance;
                    result = Direction.UP;
                }
            }
        }
        return result;
    }

    private String CheckResult(Direction result, Board board) {
        String checkedResultStone = result.toString();
        String checkedResultBomb = result.toString();
        Point me = board.getMe();
        if (me != null) {
            int x = me.getX();
            int y = me.getY();

            checkedResultStone = findBestDirectionNearStone(board, me, result).toString();
            checkedResultBomb = findBestDirectionNearBomb(board, me, result).toString();

            if (result.equals(checkedResultStone)) {
                if(checkedResultStone.equals(checkedResultBomb)){
                    return result.toString();
                }else {
                    return checkedResultBomb;
                }

            }
// else {
//                if(checkedResultStone.equals(checkedResultBomb)){
//                    return checkedResultStone;
//                }else {
//                    return checkedResultBomb;
//                }
//            }
        }
        return checkedResultStone;
    }

    private Direction findBestDirectionNearBomb(Board board, Point me, Direction givenDirection) {
        Direction bestDirection = givenDirection;

        if ((board.isBombAt(me.getX(), me.getY() - 2)) & // TODO implement directions
                (bestDirection.equals(Direction.RIGHT))){
            return Direction.RIGHT;
        }

        if ((board.isBombAt(me.getX() + 1, me.getY() - 3)) & // TODO implement directions
                (bestDirection.equals(Direction.RIGHT))){
            return Direction.LEFT;
        }

        if ((board.isBombAt(me.getX() + 2, me.getY() - 2)) & // TODO implement directions
                (bestDirection.equals(Direction.RIGHT))){
            return Direction.STOP;
        }

        if ((board.isBombAt(me.getX() + 1, me.getY() - 1)) & // TODO implement directions
                (bestDirection.equals(Direction.RIGHT))){
            return Direction.STOP;
        }

        if ((board.isBombAt(me.getX() + 1, me.getY() + 1)) & // TODO implement directions
                (bestDirection.equals(Direction.RIGHT))){
            return Direction.RIGHT;
        }

        return bestDirection;
    }

    private Direction findBestDirectionNearStone(Board board, Point me, Direction givenDirection) {
        Direction bestDirection = givenDirection;

        if ((board.isStoneAt(me.getX() - 1, me.getY() - 1)) & (bestDirection.equals(Direction.LEFT))){
            return Direction.STOP;
        }

        if ((board.isStoneAt(me.getX() + 1, me.getY() - 1)) & (bestDirection.equals(Direction.RIGHT))){
            return Direction.STOP;
        }

        if (((board.isStoneAt(me.getX(), me.getY() - 1)) ||
                (board.isStoneAt(me.getX(), me.getY() - 2))) & (bestDirection.equals(Direction.UP))){
//            if ((findBestDirectionNearStone(board, me, Direction.LEFT)).equals(bestDirection)){
            return Direction.LEFT;
//            }else {
//                return Direction.RIGHT;
//            }

//            return bestDirection;
        }


        return bestDirection;
    }

    public static void main(String[] args) {
        start(USER_NAME, WebSocketRunner.Host.REMOTE);
    }

    public static void start(String name, WebSocketRunner.Host server) {
        try {
            WebSocketRunner.run(server, name,
                    new YourSolver(new RandomDice()),
                    new Board());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
