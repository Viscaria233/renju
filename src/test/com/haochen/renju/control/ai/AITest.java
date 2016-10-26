package test.com.haochen.renju.control.ai;

import com.haochen.renju.control.ai.AI;
import com.haochen.renju.control.wintree.WinTree;
import com.haochen.renju.main.Config;
import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.PieceMap;
import com.haochen.renju.storage.Point;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * AI Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>ʮ�� 13, 2016</pre>
 */
public class AITest {

    private AI ai = new AI();

    @Before
    public void before() throws Exception {
        Config.init();
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
     * Method: getContinueAttribute(PieceMap map, PieceColor color, Point location, Direction direction)
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
     * Method: findLongContinue(PieceMap map, Point location, Direction direction)
     */
    @Test
    public void testFindLongContinue() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: isDoubleFour(PieceMap map, Point location)
     */
    @Test
    public void testIsDoubleFour() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: isDoubleThree(PieceMap map, Point location)
     */
    @Test
    public void testIsDoubleThree() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: isForbiddenMove(PieceMap map, Point location, Direction direction)
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
     * Method: getCloseMove(PieceMap map, PieceColor color, Piece lastPiece)
     */
    @Test
    public void testGetCloseMove() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getMove(PieceMap map, PieceColor color)
     */
    @Test
    public void testGetMove() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findVCF(PieceMap map, PieceColor color)
     */
    @Test
    public void testFindVCF() throws Exception {
//TODO: Test goes here...

        List<PieceMap> ques = new ArrayList<>();
        List<WinTree> ans = new ArrayList<>();
        List<WinTree> found = new ArrayList<>();
        PieceColor[] colors = {
                PieceColor.BLACK,
                PieceColor.BLACK,
                PieceColor.WHITE,
                PieceColor.BLACK,
                PieceColor.BLACK,
                PieceColor.BLACK,
        };

        ObjectInputStream ois = null;
        for (int i = 0; i < Config.Test.QuesCount.vcf; ++i) {
            ois = Config.Test.createVCFStream("vcf_question_" + i + ".ques");
            ques.add((PieceMap) ois.readObject());
            ois.close();
            ois = Config.Test.createVCFStream("vcf_answer_" + i + ".ans");
            ans.add((WinTree) ois.readObject());
            ois.close();
        }

        for (int i = 0; i < ques.size(); ++i) {
            found.add(ai.findVCF(ques.get(i), colors[i]));
        }
        Assert.assertEquals(found, ans);
    }

    /**
     * Method: findAllFourPoints(PieceMap map, PieceColor color)
     */
    @Test
    public void testFindAllFourPoints() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: findAllFivePoints(PieceMap map, PieceColor color)
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
     * Method: getAllScore(PieceMap map, PieceColor color)
     */
    @Test
    public void testGetAllScore() throws Exception {
//TODO: Test goes here... 
/* 
try { 
   Method method = AI.getClass().getMethod("getAllScore", PieceMap.class, PieceColor.class); 
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
