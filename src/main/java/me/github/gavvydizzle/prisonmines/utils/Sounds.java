package me.github.gavvydizzle.prisonmines.utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Sounds {

    public static Sounds generalClickSound, generalFailSound;
    public static Sounds increaseChanceSound, decreaseChanceSound;

    static {
        generalClickSound = new Sounds(Sound.UI_BUTTON_CLICK, 1, 1);
        generalFailSound = new Sounds(Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
        increaseChanceSound = new Sounds(Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2);
        decreaseChanceSound = new Sounds(Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
    }

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public Sounds(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    /**
     * Plays the sound at the location for all players to hear.
     * @param loc The location to play the sound.
     */
    public void playSound(Location loc) {
        if (loc.getWorld() != null) loc.getWorld().playSound(loc, sound, volume, pitch);
    }

    /**
     * Plays the sound for only the player to hear.
     * @param p The player to play the sound for.
     */
    public void playSound(Player p) {
        p.playSound(p.getLocation(), sound, volume, pitch);
    }
}