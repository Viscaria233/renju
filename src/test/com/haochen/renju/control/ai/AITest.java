package test.com.haochen.renju.control.ai;

import com.haochen.renju.storage.Cell;
import com.haochen.renju.calculate.ai.AI;
import com.haochen.renju.calculate.ai.GameTree;
import com.haochen.renju.storage.Board;
import com.haochen.renju.storage.PieceMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import test.TestConfig;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * AI Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Ê®ÔÂ 13, 2016</pre>
 */
public class AITest {

    private AI ai = new AI();

    @Before
    public void before() throws Exception {
        TestConfig.init();
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: setMediator(Mediator mediator)
     */
    @Test
    public void testSetMediator() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findWinner(PieceMap map, Piece piece)
     */
    @Test
    public void testFindWinner() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getContinueAttribute(PieceMap map, int type, Point point, Direction direction)
     */
    @Test
    public void testGetContinueAttribute() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findFive(ContinueAttribute attribute, Direction direction)
     */
    @Test
    public void testFindFive() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findAliveFour(PieceMap map, ContinueAttribute attribute, Direction direction)
     */
    @Test
    public void testFindAliveFour() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findAsleepFour(PieceMap map, ContinueAttribute attribute, Direction direction)
     */
    @Test
    public void testFindAsleepFour() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findAliveThree(PieceMap map, ContinueAttribute attribute, Direction direction)
     */
    @Test
    public void testFindAliveThree() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findAsleepThree(PieceMap map, ContinueAttribute attribute, Direction direction)
     */
    @Test
    public void testFindAsleepThree() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findLongContinue(PieceMap map, Point point, Direction direction)
     */
    @Test
    public void testFindLongContinue() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: isDoubleFour(PieceMap map, Point point)
     */
    @Test
    public void testIsDoubleFour() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: isDoubleThree(PieceMap map, Point point)
     */
    @Test
    public void testIsDoubleThree() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: isForbiddenMove(PieceMap map, Point point, Direction direction)
     */
    @Test
    public void testIsForbiddenMove() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getContinueTypes(PieceMap map, ContinueAttribute attribute)
     */
    @Test
    public void testGetContinueTypes() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getCloseMove(PieceMap map, int type, Piece lastPiece)
     */
    @Test
    public void testGetCloseMove() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getMove(PieceMap map, int type)
     */
    @Test
    public void testGetMove() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findVCF(PieceMap map, int type)
     */
    @Test
    public void testFindVCF() throws Exception {
//TODO: Test goes here...

        List<PieceMap> ques = new ArrayList<>();
        List<GameTree> ans = new ArrayList<>();
        List<GameTree> found = new ArrayList<>();
        int[] colors = {
                Cell.BLACK,
                Cell.BLACK,
                Cell.WHITE,
                Cell.BLACK,
                Cell.BLACK,
                Cell.BLACK,
        };

        ObjectInputStream ois = null;
        for (int i = 0; i < TestConfig.Test.QuesCount.vcf; ++i) {
            ois = TestConfig.Test.createVCFStream("vcf_question_" + i + ".ques");
            ques.add((PieceMap) ois.readObject());
            ois.close();
            ois = TestConfig.Test.createVCFStream("vcf_answer_" + i + ".ans");
            ans.add((GameTree) ois.readObject());
            ois.close();
        }

        for (int i = 0; i < ques.size(); ++i) {
            found.add(ai.findVCF(new Board(ques.get(i)), colors[i]));
        }
        Assert.assertEquals(found, ans);
    }

    /**
     * Method: findAllFourPoints(PieceMap map, int type)
     */
    @Test
    public void testFindAllFourPoints() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findAllFivePoints(PieceMap map, int type)
     */
    @Test
    public void testFindAllFivePoints() throws Exception {
//TODO: Test goes here... 
    }


    /**
     * Method: getContinueType(PieceMap map, ContinueAttribute attribute, Direction direction)
     */
    @Test
    public void testGetContinueType() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = AI.getClass().getMethod("getContinueType", PieceMap.class, ContinueAttribute.class, Direction.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getHighScorePoints(Map.Entry<Point, Integer>[] entries, int size)
     */
    @Test
    public void testGetHighScorePoints() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = AI.getClass().getMethod("getHighScorePoints", Map.Entry<Point,.class, int.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getAllScore(PieceMap map, int type)
     */
    @Test
    public void testGetAllScore() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = AI.getClass().getMethod("getAllScore", PieceMap.class, int.class);
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

    /**
     * Method: getScore(PieceMap map, ContinueAttribute currentPlayer, ContinueAttribute otherPlayer)
     */
    @Test
    public void testGetScore() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = AI.getClass().getMethod("getScore", PieceMap.class, ContinueAttribute.class, ContinueAttribute.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/
    }

} 
