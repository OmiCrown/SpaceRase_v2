package com.codenjoy.dojo.spacerace.client.ai;

import com.codenjoy.dojo.client.Direction;
import com.codenjoy.dojo.client.LocalGameRunner;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.spacerace.client.Board;
import com.codenjoy.dojo.spacerace.services.GameRunner;

public class AlAnTestSolver implements Solver<Board> {

    private int delay = 0;
    private boolean vpravo = true;
    private Board board;

    public AlAnTestSolver(Dice dice) {
    }

    @Override
    public String get(final Board board) {
        this.board = board;
        if (board.isGameOver()) return "";
        String result = Direction.UP.toString();
        result = findTheDirestion(board);



        return result;
    }

    private String findTheDirestion(Board board) {
        Point me = board.getMe();
        if (me != null) {
            System.out.println(me.getX() + " " + me.getY());
        }
        return "";
    }

    /**
     * Метод для запуска игры с текущим ботом. Служит для отладки.
     */
    public static void main(String[] args) {
        LocalGameRunner.run(new GameRunner(),
                new AlAnTestSolver(new RandomDice()),
                new Board());
//        start(WebSocketRunner.DEFAULT_USER, WebSocketRunner.Host.LOCAL);
    }

    public static void start(String name, WebSocketRunner.Host server) {
        try {
            WebSocketRunner.run(server, name,
                    new AlAnTestSolver(new RandomDice()),
                    new Board());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
