package test.com.haochen.renju.control.ai;

import com.haochen.renju.bean.Piece;
import com.haochen.renju.control.Mediator;
import com.haochen.renju.control.ai.AI;
import com.haochen.renju.control.wintree.WinTree;
import com.haochen.renju.storage.PieceColor;
import com.haochen.renju.storage.PieceMap;
import com.haochen.renju.storage.Point;
import com.haochen.renju.ui.TestFrame;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
    private File path = new File("renju_test");

    private ObjectInputStream createStream(String fileName) {
        File file = new File(path, fileName);
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }

    @Before
    public void before() throws Exception {
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
        List<List<Point>> ans = new ArrayList<>();
        List<List<Point>> found = new ArrayList<>();
        PieceColor[] colors = {
                PieceColor.BLACK,
                PieceColor.BLACK,
                PieceColor.WHITE,
                PieceColor.BLACK,
                PieceColor.BLACK,
                PieceColor.BLACK,
        };

        ObjectInputStream ois = null;
        for (int i = 1; i <= 6; ++i) {
            ois = createStream("vcf_question_" + i + ".pm");
            ques.add((PieceMap) ois.readObject());
            ois.close();
            ois = createStream("vcf_answer_" + i + ".pts");
            ans.add((List<Point>) ois.readObject());
            ois.close();
        }

		List<WinTree> trees = new ArrayList<>();
        for (int i = 0; i < ques.size(); ++i) {
//            found.addAllChildren(ai.findVCF(ques.get(i), colors[i]));
            trees.add(ai.findVCF(ques.get(i), colors[i], 0));
        }
        Assert.assertEquals(trees, ans);
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
