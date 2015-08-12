package com.codenjoy.dojo.spacerace.client;

import com.codenjoy.dojo.client.Direction;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Dice;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: sanja
 * Date: 05.10.13
 * Time: 11:56
 */
public class SolverTest {

    private Dice dice;
    private Solver ai;

    @Before
    public void setup() {
        dice = mock(Dice.class);
        ai = new YourSolver(dice);
    }

    private Board board(String board) {

        return (Board) new Board().forString(board);
    }

    @Test
    public void shouldUP() {
        assertAI("☼   ☼" +
                 "☼ 7 ☼" +
                 "☼   ☼" +
                 "☼ ☺ ☼" +
                 "☼   ☼"
                 , Direction.UP);
    }

    @Test
    public void shouldRight() {
        assertAI("☼   ☼" +
                 "☼☺ 7☼" +
                 "☼   ☼" +
                 "☼   ☼" +
                 "☼   ☼"
                 , Direction.RIGHT);
    }

    @Test
    public void shouldLeft() {
        assertAI("☼   ☼" +
                 "☼7 ☺☼" +
                 "☼   ☼" +
                 "☼   ☼" +
                 "☼   ☼"
                 , Direction.LEFT);
    }

    @Test
    public void shouldDown() {
        assertAI("☼   ☼" +
                 "☼  ☺☼" +
                 "☼   ☼" +
                 "☼  7☼" +
                 "☼   ☼"
                 , Direction.DOWN);
    }

    private void assertAI(String board, Direction expected) {
        String actual = ai.get(board(board));
        assertEquals(expected.toString(), actual);
    }

    private void dice(Direction direction) {
        when(dice.next(anyInt())).thenReturn(direction.value());
    }
}
