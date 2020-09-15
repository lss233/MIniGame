package dev.wwst.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zyuiop
 * @author Weiiswurst
 * I changed the ScoreboardSign to use the ProtocolLib API.
 * This means: No java.reflect, and most importantly, cross-version compatibility!!
 *
 */
public class ScoreboardSign {
    private boolean created = false;
    private final VirtualTeam[] lines = new VirtualTeam[15];
    private final Player player;
    private String objectiveName;
    private final ProtocolManager pm;

    /**
     * Create a scoreboard sign for a given player and using a specifig objective name
     * @param player the player viewing the scoreboard sign
     * @param objectiveName the name of the scoreboard sign (displayed at the top of the scoreboard)
     */
    public ScoreboardSign(final Player player, final String objectiveName) {
        this.player = player;
        this.objectiveName = objectiveName;
        this.pm = ProtocolLibrary.getProtocolManager();
    }


    private void sendPacket(PacketContainer packet) {
        try {
            pm.sendServerPacket(player,packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send the initial creation packets for this scoreboard sign. Must be called at least once.
     */
    public void create() {
        if (created)
            return;

        //final PlayerConnection player = getPlayer();
        sendPacket(createObjectivePacket(0, objectiveName));
        sendPacket(setObjectiveSlot());
        int i = 0;
        while (i < lines.length)
            sendLine(i++);

        created = true;
    }

    /**
     * Send the packets to remove this scoreboard sign. A destroyed scoreboard sign must be recreated using {@link ScoreboardSign#create()} in order
     * to be used again
     */
    public void destroy() {
        if (!created)
            return;

        sendPacket(createObjectivePacket(1, null));
        for (final VirtualTeam team : lines)
            if (team != null)
                sendPacket(team.removeTeam());

        created = false;
    }

    /**
     * Change the name of the objective. The name is displayed at the top of the scoreboard.
     * @param name the name of the objective, max 32 char
     */
    public void setObjectiveName(final String name) {
        this.objectiveName = name;
        if (created)
            sendPacket(createObjectivePacket(2, name));
    }

    /**
     * Change a scoreboard line and send the packets to the player. Can be called async.
     * @param line the number of the line (0 <= line < 15)
     * @param value the new value for the scoreboard line
     */
    public void setLine(final int line, final String value) {
        final VirtualTeam team = getOrCreateTeam(line);
        final String old = team.getCurrentPlayer();

        if(value.equals(old))
            return;

        if (old != null && created)
            sendPacket(removeLine(old));

        team.setValue(value);
        sendLine(line);
    }
    /**
     * Set all scoreboard lines to the list and send these to the player.
     * @param list The list of new scoreboard lines
     */
    public void setLines(final Iterable<String> list) {
        int i = 0;
        for(final String x : list) {
            setLine(i,x);
            i++;
        }
    }

    /**
     * Remove a given scoreboard line
     * @param line the line to remove
     */
    public void removeLine(final int line) {
        final VirtualTeam team = getOrCreateTeam(line);
        final String old = team.getCurrentPlayer();

        if (old != null && created) {
            sendPacket(removeLine(old));
            sendPacket(team.removeTeam());
        }

        lines[line] = null;
    }

    /**
     * Get the current value for a line
     * @param line the line
     * @return the content of the line
     */
    public String getLine(final int line) {
        if (line > 14)
            return null;
        if (line < 0)
            return null;
        return getOrCreateTeam(line).getValue();
    }

    /**
     * Get the team assigned to a line
     * @return the {@link VirtualTeam} used to display this line
     */
    public VirtualTeam getTeam(final int line) {
        if (line > 14)
            return null;
        if (line < 0)
            return null;
        return getOrCreateTeam(line);
    }

    /*private PlayerConnection getPlayer() {
        return ((CraftPlayer) player).getHandle().playerConnection;
    }*/

    private void sendLine(final int line) {
        if (line > 14)
            return;
        if (line < 0)
            return;
        if (!created)
            return;

        int score = (15 - line);
        VirtualTeam val = getOrCreateTeam(line);
        for (final PacketContainer packet : val.sendLine())
            sendPacket(packet);
        sendPacket(sendScore(val.getCurrentPlayer(), score));
        val.reset();
    }

    private VirtualTeam getOrCreateTeam(final int line) {
        if (lines[line] == null)
            lines[line] = new VirtualTeam("__fakeScore" + line);

        return lines[line];
    }

    /*
        Factories
         */
    private PacketContainer createObjectivePacket(final int mode, final String displayName) {
        //PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
        PacketContainer packet2 = pm.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE, true);
        packet2.getStrings().write(0,player.getName());
        packet2.getIntegers().write(0,mode);
        // Nom de l'objectif
        //setField(packet, "a", player.getName());

        // Mode
        // 0 : créer
        // 1 : Supprimer
        // 2 : Mettre à jour
        //setField(packet, "d", mode);

        if (mode == 0 || mode == 2) {
            packet2.getStrings().write(1,displayName);
            //setField(packet, "b", displayName);
            //setField(packet, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        }

        return packet2;
    }

    private PacketContainer setObjectiveSlot() {
        //PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();
        PacketContainer packet2 = pm.createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        packet2.getIntegers().write(0,1);
        packet2.getStrings().write(0,player.getName());
        // Slot
        //setField(packet, "a", 1);
        //setField(packet, "b", player.getName());

        return packet2;
    }

    private PacketContainer sendScore(final String line,final int score) {
        //PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(line);
        PacketContainer packet2 = pm.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet2.getStrings().write(0,line);
        packet2.getStrings().write(1,player.getName());
        packet2.getIntegers().write(0,score);
        packet2.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE);
        //setField(packet, "b", player.getName());
        //setField(packet, "c", score);
        //setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);

        return packet2;
    }

    private PacketContainer removeLine(String line) {
        PacketContainer packet2 = pm.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet2.getStrings().write(0,line);
        packet2.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE);
        return packet2;
    }

    /**
     * This class is used to manage the content of a line. Advanced users can use it as they want, but they are encouraged to read and understand the
     * code before doing so. Use these methods at your own risk.
     */
    private static class VirtualTeam {
        private final String name;
        private String prefix;
        private String suffix;
        private String currentPlayer;
        private String oldPlayer;
        private final ProtocolManager pm;

        private boolean prefixChanged, suffixChanged, playerChanged = false;
        private boolean first = true;

        private VirtualTeam(final String name, final String prefix, final String suffix) {
            this.name = name;
            this.prefix = prefix;
            this.suffix = suffix;
            this.pm = ProtocolLibrary.getProtocolManager();
        }

        private VirtualTeam(final String name) {
            this(name, "", "");
        }

        public String getName() {
            return name;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(final String prefix) {
            if (this.prefix == null || !this.prefix.equals(prefix))
                this.prefixChanged = true;
            this.prefix = prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(final String suffix) {
            if (this.suffix == null || !this.suffix.equals(prefix))
                this.suffixChanged = true;
            this.suffix = suffix;
        }

        private PacketContainer createPacket(final int mode) {
            //PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

            PacketContainer packet2 = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM,true);
            packet2.getStrings().write(0,name);
            packet2.getStrings().write(1,"");
            packet2.getStrings().write(2,prefix);
            packet2.getStrings().write(3,suffix);
            packet2.getStrings().write(4,"always");
            packet2.getIntegers().write(0,0);
            packet2.getIntegers().write(1,mode);
            packet2.getIntegers().write(2,0);


            //setField(packet, "a", name);
            //setField(packet, "h", mode);
            //setField(packet, "b", "");
            //setField(packet, "c", prefix);
            //setField(packet, "d", suffix);
            //setField(packet, "i", 0);
            //setField(packet, "e", "always");
            //setField(packet, "f", 0);

            return packet2;
        }

        public PacketContainer createTeam() {
            return createPacket(0);
        }

        public PacketContainer updateTeam() {
            return createPacket(2);
        }

        public PacketContainer removeTeam() {

            PacketContainer packet2 = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet2.getStrings().write(0,name);
            packet2.getIntegers().write(1,1);

            //PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            //setField(packet, "a", name);
            //setField(packet, "h", 1);
            first = true;
            return packet2;
        }

        public void setPlayer(final String name) {
            if (this.currentPlayer == null || !this.currentPlayer.equals(name))
                this.playerChanged = true;
            this.oldPlayer = this.currentPlayer;
            this.currentPlayer = name;
        }

        public Iterable<PacketContainer> sendLine() {
            List<PacketContainer> packets = new ArrayList<>();

            if (first) {
                packets.add(createTeam());
            } else if (prefixChanged || suffixChanged) {
                packets.add(updateTeam());
            }

            if (first || playerChanged) {
                if (oldPlayer != null)										// remove these two lines ?
                    packets.add(addOrRemovePlayer(4, oldPlayer)); 	//
                packets.add(changePlayer());
            }

            if (first)
                first = false;

            return packets;
        }

        public void reset() {
            prefixChanged = false;
            suffixChanged = false;
            playerChanged = false;
            oldPlayer = null;
        }

        public PacketContainer changePlayer() {
            return addOrRemovePlayer(3, currentPlayer);
        }

        public PacketContainer addOrRemovePlayer(final int mode, final String playerName) {
            //PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            //setField(packet, "a", name);
            //setField(packet, "h", mode);

            PacketContainer packet2 = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet2.getStrings().write(0,name);
            packet2.getIntegers().write(1,mode);
            Collection<String> playerNames = Lists.newArrayList(playerName);
            packet2.getSpecificModifier(Collection.class).write(0,playerNames);

            /*try {
                Field f = packet.getClass().getDeclaredField("g");
                f.setAccessible(true);
                ((List<String>) f.get(packet)).add(playerName);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }*/

            return packet2;
        }

        public String getCurrentPlayer() {
            return currentPlayer;
        }

        public String getValue() {
            return getPrefix() + getCurrentPlayer() + getSuffix();
        }

        public void setValue(final String value) {
            if (value.length() <= 16) {
                setPrefix("");
                setSuffix("");
                setPlayer(value);
            } else if (value.length() <= 32) {
                setPrefix(value.substring(0, 16));
                setPlayer(value.substring(16));
                setSuffix("");
            } else if (value.length() <= 48) {
                setPrefix(value.substring(0, 16));
                setPlayer(value.substring(16, 32));
                setSuffix(value.substring(32));
            } else {
                throw new IllegalArgumentException("Too long value ! Max 48 characters, value was " + value.length() + " !");
            }
        }
    }

    /*private static void setField(final Object edit, final String fieldName, final Object value) {
        try {
            final Field field = edit.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(edit, value);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }*/
}