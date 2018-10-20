package com.github.decyg;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.*;
import java.util.*;

class Pair implements Comparable<Pair>{
    String x;
    int y;
    public Pair(String x, int y){
        this.x = x;
        this.y = y;
    }
    public int compareTo(Pair other){
        if (this.y > other.y) return -1;
        else if (this.y < other.y) return 1;
        return 0;
    }
}

@SuppressWarnings("unchecked")
public class hgsim {
//    static
//    {
//        try
//        {
//            kb = new Scanner(new File("hgsimIn"));
//        }
//        catch (FileNotFoundException e)
//        {}
//    }
    public static void run(MessageReceivedEvent event) throws FileNotFoundException, InterruptedException {
        //Initialization
        Scanner kb = new Scanner(MainRunner.hgsimfile);
        int peaceLv = kb.nextInt();
        int bloodthirst = kb.nextInt();
        int trust = kb.nextInt();
        int weaponPowerMultiplier = 2;
        int[] chance = new int[7];
        for (int i = 0; i < 7; i++)
        {
            chance[i] = kb.nextInt();
            if (i != 0)
                chance[i] += chance[i-1];
        }

        //Events
        int gameEventNum = kb.nextInt();
        String[] gameEvent = new String[gameEventNum];
        String[] gameEventHappen = new String[gameEventNum];
        int[] gameEventProb = new int[gameEventNum];
        for (int i = 0; i < gameEventNum; i++)
        {
            gameEvent[i] = kb.next();
            kb.nextLine();
            gameEventHappen[i] = kb.nextLine();
            if (i == 0)
                gameEventProb[i] = kb.nextInt();
            else
                gameEventProb[i] = kb.nextInt()+gameEventProb[i-1];
        }

        //Player initiallization
        int pTypeNum = kb.nextInt();
        String[] pTypes = new String[pTypeNum];
        for (int i = 0; i < pTypeNum; i++)
            pTypes[i] = kb.next();
        int playerNum = kb.nextInt();
        String[] player = new String[playerNum];
        String[] playerType = new String[playerNum];
        TreeSet<Pair>[] wInv = new TreeSet[playerNum];
        String[] death = new String[playerNum];
        int[] injure = new int[playerNum];
        for (int i = 0; i < playerNum; i++)
            wInv[i] = new TreeSet<Pair>();
        boolean[] alive = new boolean[playerNum];
        Arrays.fill(alive, true);
        for (int i = 0; i < playerNum; i++)
        {
            kb.nextLine();
            player[i] = kb.nextLine();
            playerType[i] = kb.next();
        }

        //Alliance, 2 player
        int[][] hostile = new int[playerNum][playerNum];
        boolean[][] ally = new boolean[playerNum][playerNum];

        //Weapons
        int weaponTypeNum = kb.nextInt();
        String[] wTypes = new String[weaponTypeNum];
        int[][] power = new int[weaponTypeNum][pTypeNum];
        for (int i = 0; i < weaponTypeNum; i++)
        {
            wTypes[i] = kb.next();
            for (int j = 0; j < pTypeNum; j++)
                power[i][j] = kb.nextInt();
        }
        int weaponNum = kb.nextInt();
        int[] wType = new int[weaponNum];
        int[] wRare = new int[weaponNum];
        String[] weapon = new String[weaponNum];
        double[] wMultiplier = new double[weaponNum];
        for (int i = 0; i < weaponNum; i++)
        {
            kb.nextLine();
            weapon[i] = kb.nextLine();
            wType[i] = arrIndex(wTypes, kb.next());
            wMultiplier[i] = kb.nextDouble();
            wRare[i] = kb.nextInt();
            if (i != 0)
                wRare[i] += wRare[i-1];
        }
        for (int i = 0; i < playerNum; i++)
            wInv[i].add(new Pair("fists", 10));

        //Injury
        int injureNum = kb.nextInt();
        String[] injury = new String[injureNum];
        int[] iPower = new int[injureNum];
        int[] iProb = new int[injureNum];
        int[] ie = new int[injureNum];
        Arrays.fill(ie, -1);
        for (int i = 0; i < injureNum; i++)
        {
            kb.nextLine();
            injury[i] = kb.nextLine();
            String s = kb.next();
            try
            {
                iPower[i] = Integer.parseInt(s);
            }
            catch (NumberFormatException e)
            {
                ie[i] = arrIndex(gameEvent, s);
                iPower[i] = kb.nextInt();
            }
            iProb[i] = kb.nextInt();
            if (i != 0)
                iProb[i] += iProb[i-1];
        }

        //Passive Actions
        int passiveNum = kb.nextInt();
        String[] passive = new String[passiveNum];
        int[] passiveProb = new int[passiveNum];
        int[] p2at = new int[passiveNum];
        Arrays.fill(p2at, -1);
        int[] pe = new int[passiveNum];
        Arrays.fill(pe, -1);
        for (int i = 0; i < passiveNum; i++)
        {
            kb.nextLine();
            passive[i] = kb.nextLine();
            String s = kb.next();
            try
            {
                passiveProb[i] = Integer.parseInt(s);
            }
            catch (NumberFormatException e)
            {
                pe[i] = arrIndex(gameEvent, s);
                passiveProb[i] = kb.nextInt();
            }
            if (i != 0)
                passiveProb[i] += passiveProb[i-1];
        }
        for (int i = 0; i < passiveNum; i++)
        {
            for (int j = 0; j <= passive[i].length()-6; j++)
            {
                if (passive[i].substring(j, j+6).equals("Player"))
                    p2at[i] = j;
            }
        }

        //Passive Deaths
        int pasdNum = kb.nextInt();
        String[] pasd = new String[pasdNum];
        int[] pasdProb = new int[pasdNum];
        int[] p2atd = new int[pasdNum];
        Arrays.fill(p2atd, -1);
        int[] pde = new int[pasdNum];
        Arrays.fill(pde, -1);
        for (int i = 0; i < pasdNum; i++)
        {
            kb.nextLine();
            pasd[i] = kb.nextLine();
            String s = kb.next();
            try
            {
                pasdProb[i] = Integer.parseInt(s);
            }
            catch (NumberFormatException e)
            {
                pde[i] = arrIndex(gameEvent, s);
                pasdProb[i] = kb.nextInt();
            }
            if (i != 0)
                pasdProb[i] += pasdProb[i-1];
        }
        for (int i = 0; i < pasdNum; i++)
        {
            for (int j = 0; j <= pasd[i].length()-6; j++)
            {
                if (pasd[i].substring(j, j+6).equals("Player"))
                    p2atd[i] = j;
            }
        }

        //Active deaths
        int dNum = kb.nextInt();
        String[] d = new String[dNum];
        int[] dProb = new int[dNum];
        int[] datd = new int[dNum];
        Arrays.fill(datd, -1);
        for (int i = 0; i < dNum; i++)
        {
            kb.nextLine();
            d[i] = kb.nextLine();
            dProb[i] = kb.nextInt();
            if (i != 0)
                dProb[i] += dProb[i-1];
        }
        for (int i = 0; i < dNum; i++)
        {
            for (int j = 0; j <= d[i].length()-6; j++)
            {
                if (d[i].substring(j, j+6).equals("Player"))
                    datd[i] = j;
            }
        }

        //Ties
        int tNum = kb.nextInt();
        String[] tie = new String[tNum];
        int[] tProb = new int[tNum];
        int[] tatd = new int[tNum];
        Arrays.fill(tatd, -1);
        for (int i = 0; i < tNum; i++)
        {
            kb.nextLine();
            tie[i] = kb.nextLine();
            tProb[i] = kb.nextInt();
            if (i != 0)
                tProb[i] += tProb[i-1];
        }
        for (int i = 0; i < tNum; i++)
        {
            for (int j = 0; j <= tie[i].length()-6; j++)
            {
                if (tie[i].substring(j, j+6).equals("Player"))
                    tatd[i] = j;
            }
        }

        //Multiple Deaths
        int mdNum = kb.nextInt();
        String[] md = new String[mdNum];
        int[] mdProb = new int[mdNum];
        ArrayList<Integer>[] mdatd = new ArrayList[mdNum];
        for (int i = 0; i < mdNum; i++)
        {
            mdatd[i] = new ArrayList<Integer>();
            kb.nextLine();
            md[i] = kb.nextLine();
            mdProb[i] = kb.nextInt();
            if (i != 0)
                mdProb[i] += mdProb[i-1];
        }
        for (int i = 0; i < mdNum; i++)
        {
            for (int j = 0; j <= md[i].length()-6; j++)
            {
                if (md[i].substring(j, j+6).equals("Player"))
                    mdatd[i].add(j);
            }
        }

        //Start
        Arrays.fill(death, "This player has not died yet.");
        CommandHandler.message(event, "Welcome to the Hunger Games.");
        int day = 0;
        boolean[] doAction = new boolean[playerNum];
        String[] lower = new String[playerNum];
        for (int i = 0; i < playerNum; i++)
            lower[i] = player[i].toLowerCase();
        while (has(alive))
        {
            day++;
            if (day == 2)
            {
                for (int i = 1; i < chance.length; i++)
                    chance[i] -= 750;
            }
            Arrays.fill(doAction, true);
            int curgameEvent = rIndex(gameEventProb);
            CommandHandler.message(event, "Day " + day + ", " + gameEventHappen[curgameEvent]);
            int v = ("Day " + day + ", " + gameEventHappen[curgameEvent]).length()*200;
            Queue<String> cannon = new LinkedList<String>();
            ArrayList<Integer> p = new ArrayList<Integer>();
            for (int i = 0; i < playerNum; i++)
                p.add(i);
            for (int ic = 0; ic < playerNum; ic++)
            {
                int i = p.remove((int)(Math.random()*p.size()));
                if (!alive[i])
                    continue;
//                boolean con = false;
//                while (!con)
//                {
//                    name = "wait";
//                    while(name.equals("wait"))
//                    {
//                        Thread.sleep(1000);
//                    }
//                    int temp = arrIndex(lower, name);
//                    if (temp != -1)
//                        CommandHandler.message(event, death[temp]);
//                    else
//                        con = true;
//                }
                Thread.sleep(v/3);
                if (!doAction[i])
                    continue;
                doAction[i] = false;
                int[] tempProb;
                int aNum = rIndex(chance);
                int something = 0;
                for (int j = 0; j < playerNum; j++)
                {
                    if (alive[j])
                        something++;
                }
                while((something < 3 && aNum == 2) || (something < 8 && aNum == 6))
                    aNum = rIndex(chance);

                //Fight
                if (aNum == 0)
                {
                    tempProb = new int[playerNum];
                    for (int j = 0; j < playerNum; j++)
                    {
                        if (j == i)
                        {
                            if (j == 0)
                                tempProb[j] = -1;
                            else
                                tempProb[j] = tempProb[j-1];
                        }
                        tempProb[j] = bloodthirst+hostile[i][j];
                        if (ally[i][j])
                            tempProb[j] -= trust;
                        if (j != 0)
                            tempProb[j] += tempProb[j-1];
                        if (j == 0)
                        {
                            tempProb[j] = Math.max(tempProb[j], 1);
                            if (!alive[j])
                                tempProb[j] = -1;
                        }
                        else
                        {
                            tempProb[j] = Math.max(tempProb[j], tempProb[j - 1] + 1);
                            if (!alive[j])
                                tempProb[j] = tempProb[j-1];
                        }
                    }
                    int p2 = rIndex(tempProb);
                    while (p2 == i)
                        p2 = rIndex(tempProb);
                    doAction[p2] = false;
                    tempProb = new int[3];
                    tempProb[0] = peaceLv-hostile[i][p2];
                    tempProb[1] = tempProb[0]+wInv[i].first().y*weaponPowerMultiplier-injure[i];
                    tempProb[2] = tempProb[0]+wInv[p2].first().y*weaponPowerMultiplier-injure[p2];
                    int result = rIndex(tempProb);
                    if (result == 0)
                    {
                        hostile[i][p2] += wInv[i].first().y*weaponPowerMultiplier-injure[i];
                        hostile[p2][i] = hostile[i][p2];
                        int cur = injure[i];
                        injure[i] += (wInv[p2].first().y*weaponPowerMultiplier-injure[p2])/2;
                        injure[p2] += (wInv[i].first().y*weaponPowerMultiplier-cur)/2;
                        int temp = rIndex(tProb);
                        CommandHandler.message(event, player[i] + " " + tie[temp].substring(0, tatd[temp]) + player[p2] + tie[temp].substring(tatd[temp]+6));
                        v = (player[i] + " " + tie[temp].substring(0, tatd[temp]) + player[p2] + tie[temp].substring(tatd[temp]+6)).length()*200;
                        ally[i][p2] = false;
                        ally[p2][i] = false;
                    }
                    else if (result == 1)
                    {
                        int temp = rIndex(dProb);
                        alive[p2] = false;
                        death[p2] = player[i] + ", equipped with " + wInv[i].first().x + ", " + d[temp].substring(0, datd[temp]) + player[p2] + d[temp].substring(datd[temp]+6);
                        v = death[p2].length()*200;
                        cannon.add(player[p2]);
                        CommandHandler.message(event, death[p2]);
                    }
                    else
                    {
                        int temp = rIndex(dProb);
                        alive[i] = false;
                        death[i] = player[p2] + ", equipped with " + wInv[p2].first().x + ", " + d[temp].substring(0, datd[temp]) + player[i] + d[temp].substring(datd[temp]+6);
                        v = death[i].length()*200;
                        cannon.add(player[i]);
                        CommandHandler.message(event, death[i]);
                    }
                }

                //Receive
                if (aNum == 1)
                {
                    int get = rIndex(wRare);
                    wInv[i].add(new Pair(weapon[get], (int)(wMultiplier[get]*power[wType[get]][arrIndex(pTypes, playerType[i])])));
                    if (weapon[get].charAt(weapon[get].length()-1) == 's')
                    {
                        CommandHandler.message(event, player[i] + " now has " + weapon[get] + ".");
                        v = (player[i] + " now has " + weapon[get] + ".").length()*200;
                    }
                    else
                    {
                        CommandHandler.message(event, player[i] + " now has a " + weapon[get] + ".");
                        v = (player[i] + " now has a " + weapon[get] + ".").length()*200;
                    }
                }

                //Ally
                if (aNum == 2)
                {
                    tempProb = new int[playerNum-1];
                    int top = 0;
                    for (int j = 0; j < playerNum; j++)
                    {
                        top = Math.max(top, hostile[i][j]+1);
                    }
                    for (int j = 0; j < playerNum; j++)
                    {
                        if (j == i)
                            continue;
                        int add = 0;
                        if (j > i)
                            add = 1;
                        tempProb[j-add] = top - hostile[i][j];
                        if (j-add == 0)
                            tempProb[j-add] = Math.max(1, tempProb[j-add]);
                        else
                            tempProb[j-add] += tempProb[j-add-1];
                    }
                    int now = rIndex(tempProb);
                    if (now >= i)
                        now++;
                    while (now == i || !alive[now])
                        now = rIndex(tempProb);
                    ally[i][now] = true;
                    ally[now][i] = true;
                    CommandHandler.message(event, player[i] + " has allied with " + player[now] + ".");
                    v = (player[i] + " has allied with " + player[now] + ".").length()*200;
                }

                //Injure
                if (aNum == 3)
                {
                    int now = rIndex(iProb);
                    while (ie[now] != -1 && ie[now] != curgameEvent)
                        now = rIndex(iProb);
                    injure[i] += iPower[now];
                    v = (player[i] + " " + injury[now]).length()*200;
                    CommandHandler.message(event, player[i] + " " + injury[now]);
                }

                //Passive Action
                if (aNum == 4)
                {
                    int k = rIndex(passiveProb);
                    while (pe[k] != -1 && pe[k] != curgameEvent)
                        k = rIndex(passiveProb);
                    String s = passive[k];
                    if (p2at[k] != -1)
                    {
                        tempProb = new int[playerNum];
                        for (int j = 0; j < playerNum; j++)
                        {
                            if (j == i || !alive[j])
                            {
                                if (j == 0)
                                    tempProb[j] = -1;
                                else
                                    tempProb[j] = tempProb[j-1];
                            }
                            else
                            {
                                if (j == 0)
                                    tempProb[j] = 10;
                                else
                                    tempProb[j] = tempProb[j-1]+10;
                            }
                        }
                        int p2 = rIndex(tempProb);
                        s = s.substring(0, p2at[k]) + player[p2] + s.substring(p2at[k]+6);
                    }
                    v = (player[i] + " " + s).length()*200;
                    CommandHandler.message(event, player[i] + " " + s);
                }

                //Passive Death
                if (aNum == 5)
                {
                    int k = rIndex(pasdProb);
                    while (pde[k] != -1 && pde[k] != curgameEvent)
                        k = rIndex(pasdProb);
                    String s = pasd[k];
                    if (p2atd[k] != -1)
                    {
                        tempProb = new int[playerNum];
                        for (int j = 0; j < playerNum; j++)
                        {
                            if (j == i || !alive[j])
                            {
                                if (j == 0)
                                    tempProb[j] = -1;
                                else
                                    tempProb[j] = tempProb[j-1];
                            }
                            else
                            {
                                if (j == 0)
                                    tempProb[j] = 10;
                                else
                                    tempProb[j] = tempProb[j-1]+10;
                            }
                        }
                        int p2 = rIndex(tempProb);
                        s = s.substring(0, p2atd[k]) + player[p2] + s.substring(p2atd[k]+6);
                    }
                    v = (player[i] + " " + s).length()*200;
                    CommandHandler.message(event, player[i] + " " + s);
                    cannon.add(player[i]);
                    alive[i] = false;
                }

                //Multiple Deaths
                if (aNum == 6)
                {
                    int k = rIndex(mdProb);
                    String s;
                    if (md[k].substring(0, 4).equals("User"))
                        s = player[i] + md[k].substring(4);
                    else
                        s = md[k];
                    tempProb = new int[playerNum];
                    for (int j = 0; j < playerNum; j++)
                    {
                        if (j == i || !alive[j])
                        {
                            if (j == 0)
                                tempProb[j] = -1;
                            else
                                tempProb[j] = tempProb[j-1];
                        }
                        else
                        {
                            if (j == 0)
                                tempProb[j] = 10;
                            else
                                tempProb[j] = tempProb[j-1]+10;
                        }
                    }
                    int p2 = rIndex(tempProb);
                    ArrayList<Integer> arr = new ArrayList<Integer>();
                    while (arr.size() < mdatd[k].size())
                    {
                        alive[p2] = false;
                        cannon.add(player[p2]);
                        arr.add(p2);
                        while (arr.contains(p2))
                            p2 = rIndex(tempProb);
                    }
                    int stLength = 0;
                    if (md[k].substring(0, 4).equals("User"))
                        stLength += player[i].length()-4;
                    for (int j = 0; j < mdatd[k].size(); j++)
                    {
                        s = s.substring(0, mdatd[k].get(j)+stLength) + player[arr.get(j)] + s.substring(mdatd[k].get(j)+6+stLength);
                        stLength += player[arr.get(j)].length()-6;
                    }
                    v = (s).length()*200;
                    CommandHandler.message(event, s);
                }
            }
            if (!has(alive))
            {
                break;
            }
            if (cannon.size() > 0)
            {
                CommandHandler.message(event, "\nDeaths:");
                while(!cannon.isEmpty())
                    CommandHandler.message(event, cannon.poll());
            }
            else
                CommandHandler.message(event, "\nThere were no deaths today.");
        }
        for (int i = 0; i < playerNum; i++)
        {
            if (alive[i])
            {
                CommandHandler.message(event, player[i] + " has won the Hunger Games. RIP everyone else.");
                break;
            }
        }
    }
    public static int arrIndex(String[] arr, String key)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i].equals(key))
                return i;
        }
        return -1;
    }
    public static boolean has(boolean[] arr)
    {
        int ans = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i])
                ans++;
        }
        return ans > 1;
    }
    public static int arrIndex(int[] arr, int key)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] == key)
                return i;
        }
        return -1;
    }
    public static int rIndex(int[] prob)
    {
        int x = (int)(Math.random()*prob[prob.length-1])+1;
        int now = Arrays.binarySearch(prob, x);
        if (now < 0)
            now = -(now+1);
        else
            now = arrIndex(prob, x);
        return now;
    }

}

