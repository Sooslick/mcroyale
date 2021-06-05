package ru.sooslick.royale.util;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class TeamColorUtil {
    private static LinkedList<ChatColor> availableColors = new LinkedList<>();

    public static ChatColor getRandomColor() {
        if (availableColors.isEmpty())
            reset();
        return availableColors.pop();
    }

    private static void reset() {
        availableColors = new LinkedList<>(Arrays.asList(ChatColor.values()));
        Collections.shuffle(availableColors);
    }
}
