package com.stackquest.lab10;

import java.math.BigDecimal;
import java.util.*;

/** REFERENCE / SPOILER. */
public final class Solutions {
    private Solutions() { }
    public static int[] pairTransactions(double[] a, double target) {
        Map<Double,Integer> seen=new HashMap<>(); for(int i=0;i<a.length;i++){double need=target-a[i];if(seen.containsKey(need))return new int[]{seen.get(need),i};seen.putIfAbsent(a[i],i);}return null;
    }
    public static Map<String,BigDecimal> transactionSummary(List<String> lines){Map<String,BigDecimal> out=new HashMap<>();for(String line:lines){String[] p=line.split("\\|",-1);if(p.length!=4||!"APPROVED".equals(p[3]))continue;try{out.merge(p[1],new BigDecimal(p[2]),BigDecimal::add);}catch(NumberFormatException ignored){}}return out;}
    public static boolean balancedEvents(String s){Deque<Character>d=new ArrayDeque<>();for(char c:s.toCharArray()){if("([{<".indexOf(c)>=0)d.push(c);else if(")]}>".indexOf(c)>=0){if(d.isEmpty()||"([{<".indexOf(d.pop())!=")]}>".indexOf(c))return false;}}return d.isEmpty();}
    public static int growthStreak(int[] a){if(a.length==0)return 0;int[]dp=new int[a.length];Arrays.fill(dp,1);int best=1;for(int i=0;i<a.length;i++)for(int j=0;j<i;j++)if(a[j]<a[i])best=Math.max(best,dp[i]=Math.max(dp[i],dp[j]+1));return best;}
    public static int peakThenDecline(int[]a){if(a.length==0)return 0;int n=a.length;int[]inc=new int[n],dec=new int[n];Arrays.fill(inc,1);Arrays.fill(dec,1);for(int i=0;i<n;i++)for(int j=0;j<i;j++)if(a[j]<a[i])inc[i]=Math.max(inc[i],inc[j]+1);for(int i=n-1;i>=0;i--)for(int j=n-1;j>i;j--)if(a[j]<a[i])dec[i]=Math.max(dec[i],dec[j]+1);int best=1;for(int i=0;i<n;i++)best=Math.max(best,inc[i]+dec[i]-1);return best;}
    public static int minRewards(int[]coins,int target){int[]dp=new int[target+1];Arrays.fill(dp,target+1);dp[0]=0;for(int amount=1;amount<=target;amount++)for(int coin:coins)if(coin<=amount)dp[amount]=Math.min(dp[amount],dp[amount-coin]+1);return dp[target]>target?-1:dp[target];}
    public static int fraudClusters(char[][]g){if(g.length==0)return 0;boolean[][]v=new boolean[g.length][];for(int r=0;r<g.length;r++)v[r]=new boolean[g[r].length];int n=0;int[][]ds={{1,0},{-1,0},{0,1},{0,-1}};for(int r=0;r<g.length;r++)for(int c=0;c<g[r].length;c++)if(g[r][c]=='1'&&!v[r][c]){n++;Deque<int[]>q=new ArrayDeque<>();q.add(new int[]{r,c});v[r][c]=true;while(!q.isEmpty()){int[]x=q.remove();for(int[]d:ds){int nr=x[0]+d[0],nc=x[1]+d[1];if(nr>=0&&nr<g.length&&nc>=0&&nc<g[nr].length&&g[nr][nc]=='1'&&!v[nr][nc]){v[nr][nc]=true;q.add(new int[]{nr,nc});}}}}return n;}
    public static int maxNonOverlapping(int[][]w){Arrays.sort(w,Comparator.comparingInt(x->x[1]));int count=0,end=Integer.MIN_VALUE;for(int[]x:w)if(x[0]>=end){count++;end=x[1];}return count;}
}
