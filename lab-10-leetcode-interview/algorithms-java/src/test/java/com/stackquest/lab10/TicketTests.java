package com.stackquest.lab10;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TicketTests {
 @Test @Tag("E1") @Tag("public") void e1(){assertArrayEquals(new int[]{0,1},Solutions.pairTransactions(new double[]{20,70,110},90));}
 @Test @Tag("E1") @Tag("evaluator") void e1Edges(){assertNull(Solutions.pairTransactions(new double[]{1,2},9));assertArrayEquals(new int[]{0,1},Solutions.pairTransactions(new double[]{30,30},60));}
 @Test @Tag("E2") @Tag("public") void e2(){assertEquals(Map.of("u",new BigDecimal("3.00")),Solutions.transactionSummary(List.of("t|u|1.00|APPROVED","t|u|2.00|APPROVED")));}
 @Test @Tag("E2") @Tag("evaluator") void e2Edges(){assertEquals(Map.of(),Solutions.transactionSummary(List.of("bad","t|u|x|APPROVED")));}
 @Test @Tag("E3") @Tag("public") void e3(){assertTrue(Solutions.balancedEvents("PAYMENT[AUTH{OK}]") );}
 @Test @Tag("E3") @Tag("evaluator") void e3Edges(){assertFalse(Solutions.balancedEvents("([)]"));assertTrue(Solutions.balancedEvents(""));}
 @Test @Tag("I1") @Tag("public") void i1(){assertEquals(5,Solutions.growthStreak(new int[]{7,2,8,3,9,4,10,1,11}));}
 @Test @Tag("I1") @Tag("evaluator") void i1Edges(){assertEquals(0,Solutions.growthStreak(new int[]{}));assertEquals(1,Solutions.growthStreak(new int[]{2,2}));}
 @Test @Tag("I2") @Tag("public") void i2(){assertEquals(6,Solutions.peakThenDecline(new int[]{3,9,5,12,7,15,4,2}));}
 @Test @Tag("I3") @Tag("public") void i3(){assertEquals(2,Solutions.minRewards(new int[]{1,3,4},6));}
 @Test @Tag("I4") @Tag("public") void i4(){assertEquals(2,Solutions.fraudClusters(new char[][]{{'1','0'},{'0','1'}}));}
 @Test @Tag("I4") @Tag("evaluator") void i4Edges(){assertEquals(0,Solutions.fraudClusters(new char[][]{}));}
 @Test @Tag("I5") @Tag("public") void i5(){assertEquals(3,Solutions.maxNonOverlapping(new int[][]{{1,3},{2,4},{3,5},{5,7}}));}
 @Test @Tag("I5") @Tag("evaluator") void i5Edges(){assertEquals(0,Solutions.maxNonOverlapping(new int[][]{}));assertEquals(2,Solutions.maxNonOverlapping(new int[][]{{0,2},{2,3}}));}
}
