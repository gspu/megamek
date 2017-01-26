/*
 * MegaMek - Copyright (C) 2000,2001,2002,2003,2004 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */

/*
 * CustomMechDialog.java
 *
 * Created on March 18, 2002, 2:56 PM
 */

package megamek.client.ui.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import megamek.client.Client;
import megamek.client.ui.GBC;
import megamek.client.ui.Messages;
import megamek.common.Aero;
import megamek.common.BattleArmor;
import megamek.common.Compute;
import megamek.common.Configuration;
import megamek.common.Crew;
import megamek.common.Dropship;
import megamek.common.Entity;
import megamek.common.EntitySelector;
import megamek.common.EquipmentType;
import megamek.common.GunEmplacement;
import megamek.common.IGame;
import megamek.common.IPlayer;
import megamek.common.Infantry;
import megamek.common.Mech;
import megamek.common.MiscType;
import megamek.common.Mounted;
import megamek.common.OffBoardDirection;
import megamek.common.Protomech;
import megamek.common.Tank;
import megamek.common.TechConstants;
import megamek.common.VTOL;
import megamek.common.WeaponType;
import megamek.common.options.IOption;
import megamek.common.options.IOptionGroup;
import megamek.common.options.OptionsConstants;
import megamek.common.options.PartialRepairs;
import megamek.common.options.PilotOptions;
import megamek.common.options.Quirks;
import megamek.common.options.WeaponQuirks;
import megamek.common.preference.PreferenceManager;
import megamek.common.verifier.EntityVerifier;
import megamek.common.verifier.TestAero;
import megamek.common.verifier.TestBattleArmor;
import megamek.common.verifier.TestEntity;
import megamek.common.verifier.TestInfantry;
import megamek.common.verifier.TestMech;
import megamek.common.verifier.TestSupportVehicle;
import megamek.common.verifier.TestTank;

/**
 * A dialog that a player can use to customize his mech before battle.
 * Currently, changing pilots, setting up C3 networks, changing ammunition,
 * deploying artillery offboard, setting MGs to rapidfire, setting auto-eject is
 * supported.
 *
 * @author Ben
 */
public class CustomMechDialog extends ClientDialog implements ActionListener,
        DialogOptionListener, ItemListener {

    /**
     *
     */
    private static final long serialVersionUID = -6809436986445582731L;

    public static int DONE = 0;
    public static int NEXT = 1;
    public static int PREV = 2;

    private JPanel panPilot;
    private JPanel panDeploy;
    private QuirksPanel panQuirks;
    private JPanel panPartReps;

    private JScrollPane scrPilot;
    private JScrollPane scrEquip;
    private JScrollPane scrDeploy;
    private JScrollPane scrQuirks;
    private JScrollPane scrPartreps;

    private JPanel panOptions;
    // private JScrollPane scrOptions;

    private JTabbedPane tabAll;

    private JLabel labName = new JLabel(
            Messages.getString("CustomMechDialog.labName"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldName = new JTextField(20);

    private JLabel labNick = new JLabel(
            Messages.getString("CustomMechDialog.labNick"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldNick = new JTextField(20);

    private JLabel labGunnery = new JLabel(
            Messages.getString("CustomMechDialog.labGunnery"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldGunnery = new JTextField(3);

    private JLabel labGunneryL = new JLabel(
            Messages.getString("CustomMechDialog.labGunneryL"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldGunneryL = new JTextField(3);

    private JLabel labGunneryM = new JLabel(
            Messages.getString("CustomMechDialog.labGunneryM"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldGunneryM = new JTextField(3);

    private JLabel labGunneryB = new JLabel(
            Messages.getString("CustomMechDialog.labGunneryB"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldGunneryB = new JTextField(3);

    private JLabel labPiloting = new JLabel(
            Messages.getString("CustomMechDialog.labPiloting"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldPiloting = new JTextField(3);

    private JLabel labArtillery = new JLabel(
            Messages.getString("CustomMechDialog.labArtillery"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldArtillery = new JTextField(3);

    private JLabel labFatigue = new JLabel(
            Messages.getString("CustomMechDialog.labFatigue"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldFatigue = new JTextField(3);

    private JLabel labTough = new JLabel(
            Messages.getString("CustomMechDialog.labTough"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldTough = new JTextField(3);

    private JLabel labInit = new JLabel(
            Messages.getString("CustomMechDialog.labInit"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldInit = new JTextField(3);

    private JLabel labCommandInit = new JLabel(
            Messages.getString("CustomMechDialog.labCommandInit"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldCommandInit = new JTextField(3);

    private JLabel labCallsign = new JLabel(
            Messages.getString("CustomMechDialog.labCallsign"), SwingConstants.CENTER); //$NON-NLS-1$

    private JLabel labUnitNum = new JLabel(
            Messages.getString("CustomMechDialog.labUnitNum"), SwingConstants.CENTER); //$NON-NLS-1$

    private JComboBox<String> choUnitNum = new JComboBox<String>();

    private ArrayList<Entity> entityUnitNum = new ArrayList<Entity>();

    private JLabel labDeploymentRound = new JLabel(
            Messages.getString("CustomMechDialog.labDeployment"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JLabel labDeploymentZone = new JLabel(
            Messages.getString("CustomMechDialog.labDeploymentZone"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JComboBox<String> choDeploymentRound = new JComboBox<String>();
    
    private JComboBox<String> choDeploymentZone = new JComboBox<String>();

    private JLabel labDeployShutdown = new JLabel(
            Messages.getString("CustomMechDialog.labDeployShutdown"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JCheckBox chDeployShutdown = new JCheckBox();

    private JLabel labDeployProne = new JLabel(
            Messages.getString("CustomMechDialog.labDeployProne"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JCheckBox chDeployProne = new JCheckBox();

    private JLabel labDeployHullDown = new JLabel(
            Messages.getString("CustomMechDialog.labDeployHullDown"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JCheckBox chDeployHullDown = new JCheckBox();

    private JLabel labCommander = new JLabel(
            Messages.getString("CustomMechDialog.labCommander"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JCheckBox chCommander = new JCheckBox();

    private JLabel labHidden = new JLabel(
            Messages.getString("CustomMechDialog.labHidden"), //$NON-NLS-1$
            SwingConstants.RIGHT);

    private JCheckBox chHidden = new JCheckBox();

    private JLabel labOffBoard = new JLabel(
            Messages.getString("CustomMechDialog.labOffBoard"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JCheckBox chOffBoard = new JCheckBox();

    private JLabel labOffBoardDirection = new JLabel(
            Messages.getString("CustomMechDialog.labOffBoardDirection"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JComboBox<String> choOffBoardDirection = new JComboBox<String>();

    private JLabel labOffBoardDistance = new JLabel(
            Messages.getString("CustomMechDialog.labOffBoardDistance"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldOffBoardDistance = new JTextField(4);

    private JButton butOffBoardDistance = new JButton("0");

    private JLabel labStartVelocity = new JLabel(
            Messages.getString("CustomMechDialog.labStartVelocity"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldStartVelocity = new JTextField(3);

    private JLabel labStartAltitude = new JLabel(
            Messages.getString("CustomMechDialog.labStartAltitude"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldStartAltitude = new JTextField(3);

    private JLabel labStartHeight = new JLabel(
            Messages.getString("CustomMechDialog.labStartHeight"), SwingConstants.RIGHT); //$NON-NLS-1$

    private JTextField fldStartHeight = new JTextField(3);

    private JPanel panButtons = new JPanel();

    private JButton butRandomName = new JButton(
            Messages.getString("CustomMechDialog.RandomName")); //$NON-NLS-1$

    private JButton butRandomSkill = new JButton(
            Messages.getString("CustomMechDialog.RandomSkill")); //$NON-NLS-1$

    private JButton butOkay = new JButton(Messages.getString("Okay")); //$NON-NLS-1$

    private JButton butCancel = new JButton(Messages.getString("Cancel")); //$NON-NLS-1$

    private JButton butNext = new JButton(Messages.getString("Next"));

    private JButton butPrev = new JButton(Messages.getString("Previous"));

    private EquipChoicePanel m_equip;

    private JPanel panEquip = new JPanel();

    private List<Entity> entities;

    private boolean okay;

    private int status = CustomMechDialog.DONE;

    ClientGUI clientgui;

    Client client;

    private PilotOptions options;
    private Quirks quirks;
    private PartialRepairs partReps;
    private HashMap<Integer, WeaponQuirks> h_wpnQuirks = new HashMap<Integer, WeaponQuirks>();

    private ArrayList<DialogOptionComponent> optionComps = new ArrayList<DialogOptionComponent>();
    // private ArrayList<DialogOptionComponent> quirkComps = new
    // ArrayList<DialogOptionComponent>();
    private ArrayList<DialogOptionComponent> partRepsComps = new ArrayList<DialogOptionComponent>();
    // private HashMap<Integer, ArrayList<DialogOptionComponent>>
    // h_wpnQuirkComps = new HashMap<Integer,
    // ArrayList<DialogOptionComponent>>();

    private boolean editable;

    private OffBoardDirection direction = OffBoardDirection.NONE;

    private int distance = 17;

    private JButton butPortrait;
    PortraitChoiceDialog portraitDialog;

    /**
     * Creates new CustomMechDialog
     */
    public CustomMechDialog(ClientGUI clientgui, Client client, List<Entity> entities,
            boolean editable) {
        super(clientgui.frame,
                Messages.getString("CustomMechDialog.title"), true); //$NON-NLS-1$

        this.entities = entities;
        this.clientgui = clientgui;
        this.client = client;

        // Ensure we have at least one passed entity
        //  Anything less makes no sense
        if (entities.size() < 1) {
            throw new IllegalStateException("Must pass at least one Entity!");
        }

        boolean multipleEntities = entities.size() > 1;
        boolean quirksEnabled = clientgui.getClient().getGame().getOptions()
                .booleanOption(OptionsConstants.ADVANCED_STRATOPS_QUIRKS);
        boolean partialRepairsEnabled = clientgui.getClient().getGame()
                .getOptions().booleanOption(OptionsConstants.ADVANCED_STRATOPS_PARTIALREPAIRS);
        final Entity entity = entities.get(0);
        boolean isAero = true;
        boolean isInfantry = true;
        boolean isMech = true;
        boolean isProtomech = true;
        boolean isTank = true;
        boolean isVTOL = true;
        boolean eligibleForOffBoard = true;
        
        for (Entity e : entities) {
            isAero &= e instanceof Aero;
            isInfantry &= e instanceof Infantry;
            isMech &= e instanceof Mech;
            isTank &= e instanceof Tank;
            isProtomech &= e instanceof Protomech;
            isVTOL &= e instanceof VTOL;
            boolean entityEligibleForOffBoard = false;
            for (Mounted mounted : e.getWeaponList()) {
                WeaponType wtype = (WeaponType) mounted.getType();
                if (wtype.hasFlag(WeaponType.F_ARTILLERY)) {
                    entityEligibleForOffBoard = true;
                }
            }
            eligibleForOffBoard &= entityEligibleForOffBoard;
        }

        // set up the panels
        JPanel mainPanel = new JPanel(new GridBagLayout());
        tabAll = new JTabbedPane();

        panPilot = new JPanel(new GridBagLayout());
        panDeploy = new JPanel(new GridBagLayout());
        quirks = entity.getQuirks();
        panQuirks = new QuirksPanel(entity, quirks, editable, this, h_wpnQuirks);
        panPartReps = new JPanel(new GridBagLayout());
        setupEquip();

        scrPilot = new JScrollPane(panPilot);
        scrEquip = new JScrollPane(panEquip);
        scrDeploy = new JScrollPane(panDeploy);
        scrQuirks = new JScrollPane(panQuirks);
        scrPartreps = new JScrollPane(panPartReps);

        panOptions = new JPanel(new GridBagLayout());

        mainPanel.add(tabAll,
                GBC.eol().fill(GridBagConstraints.BOTH).insets(5, 5, 5, 5));
        mainPanel.add(panButtons, GBC.eol().anchor(GridBagConstraints.CENTER));

        if (!multipleEntities) {
        tabAll.addTab(Messages.getString("CustomMechDialog.tabPilot"), scrPilot);
        tabAll.addTab(Messages.getString("CustomMechDialog.tabEquipment"),
                scrEquip);
        }
        tabAll.addTab(Messages.getString("CustomMechDialog.tabDeployment"),
                scrDeploy);
        if (quirksEnabled && !multipleEntities) {
            scrQuirks.setPreferredSize(scrEquip.getPreferredSize());
            tabAll.addTab("Quirks", scrQuirks);
        }
        if (partialRepairsEnabled && !multipleEntities) {
            tabAll.addTab(
                    Messages.getString("CustomMechDialog.tabPartialRepairs"),
                    scrPartreps);
        }
        getContentPane().add(mainPanel);

        options = entity.getCrew().getOptions();
        partReps = entity.getPartialRepairs();
        for (Mounted m : entity.getWeaponList()) {
            h_wpnQuirks.put(entity.getEquipmentNum(m), m.getQuirks());
        }
        // Also need to consider melee weapons
        for (Mounted m : entity.getMisc()) {
            if (m.getType().hasFlag(MiscType.F_CLUB)) {
                h_wpnQuirks.put(entity.getEquipmentNum(m), m.getQuirks());
            }
        }
        this.editable = editable;

        // **PILOT TAB**/
        if (isTank) {
            labPiloting.setText(Messages
                    .getString("CustomMechDialog.labDriving"));
        } else if (isInfantry) {
            labPiloting.setText(Messages
                    .getString("CustomMechDialog.labAntiMech"));
        } else {
            labPiloting.setText(Messages
                    .getString("CustomMechDialog.labPiloting"));
        }

        butPortrait = new JButton();
        butPortrait.setPreferredSize(new Dimension(72, 72));
        butPortrait.setText(Messages.getString("CustomMechDialog.labPortrait"));
        butPortrait.setActionCommand("portrait"); //$NON-NLS-1$
        butPortrait.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                portraitDialog.setVisible(true);
            }
        });
        portraitDialog = new PortraitChoiceDialog(clientgui.getFrame(),
                butPortrait);
        portraitDialog.setPilot(entity.getCrew());

        panPilot.add(butPortrait, GBC.std().gridheight(2));
        panPilot.add(butRandomName, GBC.eop());
        panPilot.add(butRandomSkill, GBC.eop());

        panPilot.add(labName, GBC.std());
        panPilot.add(fldName, GBC.eol());

        panPilot.add(labNick, GBC.std());
        panPilot.add(fldNick, GBC.eop());

        if (client.getGame().getOptions().booleanOption(OptionsConstants.RPG_RPG_GUNNERY)) {

            panPilot.add(labGunneryL, GBC.std());
            panPilot.add(fldGunneryL, GBC.eol());

            panPilot.add(labGunneryM, GBC.std());
            panPilot.add(fldGunneryM, GBC.eol());

            panPilot.add(labGunneryB, GBC.std());
            panPilot.add(fldGunneryB, GBC.eol());

        } else {
            panPilot.add(labGunnery, GBC.std());
            panPilot.add(fldGunnery, GBC.eol());
        }

        panPilot.add(labPiloting, GBC.std());
        panPilot.add(fldPiloting, GBC.eop());

        if (client.getGame().getOptions().booleanOption(OptionsConstants.RPG_ARTILLERY_SKILL)) {
            panPilot.add(labArtillery, GBC.std());
            panPilot.add(fldArtillery, GBC.eop());
        }

        if (client.getGame().getOptions().booleanOption(OptionsConstants.ADVANCED_TACOPS_FATIGUE)) {
            labFatigue.setToolTipText(Messages
                    .getString("CustomMechDialog.labFatigueToolTip"));
            panPilot.add(labFatigue, GBC.std());
            panPilot.add(fldFatigue, GBC.eop());
        }

        if (client.getGame().getOptions().booleanOption(OptionsConstants.RPG_TOUGHNESS)) {
            panPilot.add(labTough, GBC.std());
            panPilot.add(fldTough, GBC.eop());
        }

        if (client.getGame().getOptions()
                .booleanOption(OptionsConstants.RPG_INDIVIDUAL_INITIATIVE)) {
            panPilot.add(labInit, GBC.std());
            panPilot.add(fldInit, GBC.eop());
        }

        if (client.getGame().getOptions().booleanOption(OptionsConstants.RPG_COMMAND_INIT)) {
            panPilot.add(labCommandInit, GBC.std());
            panPilot.add(fldCommandInit, GBC.eop());
        }

        // Set up commanders for commander killed victory condition
        if (clientgui.getClient().getGame().getOptions()
                .booleanOption(OptionsConstants.VICTORY_COMMANDER_KILLED)) { //$NON-NLS-1$
            panPilot.add(labCommander, GBC.std());
            panPilot.add(chCommander, GBC.eol());
            chCommander.setSelected(entity.isCommander());
        }

        if (isProtomech) {
            // All Protomechs have a callsign.
            StringBuffer callsign = new StringBuffer(
                    Messages.getString("CustomMechDialog.Callsign")); //$NON-NLS-1$
            callsign.append(": "); //$NON-NLS-1$
            callsign.append(
                    (entity.getUnitNumber() + PreferenceManager
                            .getClientPreferences().getUnitStartChar()))
                    .append('-').append(entity.getId());
            labCallsign.setText(callsign.toString());
            panPilot.add(labCallsign,
                    GBC.eol().anchor(GridBagConstraints.CENTER));

            // Get the Protomechs of this entity's player
            // that *aren't* in the entity's unit.
            Iterator<Entity> otherUnitEntities = client.getGame()
                    .getSelectedEntities(new EntitySelector() {
                        private final int ownerId = entity.getOwnerId();

                        private final short unitNumber = entity.getUnitNumber();

                        public boolean accept(Entity unitEntity) {
                            if ((unitEntity instanceof Protomech)
                                    && (ownerId == unitEntity.getOwnerId())
                                    && (unitNumber != unitEntity
                                            .getUnitNumber())) {
                                return true;
                            }
                            return false;
                        }
                    });

            // If we got any other entites, show the unit number controls.
            if (otherUnitEntities.hasNext()) {
                panPilot.add(labUnitNum, GBC.std());
                panPilot.add(choUnitNum, GBC.eop());
                refreshUnitNum(otherUnitEntities);
            }
        }

        if (clientgui.getClient().getGame().getOptions()
                .booleanOption(OptionsConstants.RPG_PILOT_ADVANTAGES) //$NON-NLS-1$
                || clientgui.getClient().getGame().getOptions()
                        .booleanOption(OptionsConstants.EDGE) //$NON-NLS-1$
                || clientgui.getClient().getGame().getOptions()
                        .booleanOption(OptionsConstants.RPG_MANEI_DOMINI)) { //$NON-NLS-1$

            panPilot.add(panOptions, GBC.eop());
        }

        // **DEPLOYMENT TAB**//
        if (isAero) {
            panDeploy.add(labStartVelocity, GBC.std());
            panDeploy.add(fldStartVelocity, GBC.eol());

            panDeploy.add(labStartAltitude, GBC.std());
            panDeploy.add(fldStartAltitude, GBC.eol());
        }
        if (isVTOL) {
            panDeploy.add(labStartHeight, GBC.std());
            panDeploy.add(fldStartHeight, GBC.eol());
        }

        choDeploymentRound.addItemListener(this);
        labDeploymentZone.setToolTipText(Messages
                .getString("CustomMechDialog.deployZoneToolTip")); //$NON-NLS-1$
        choDeploymentZone.setToolTipText(Messages
                .getString("CustomMechDialog.deployZoneToolTip")); //$NON-NLS-1$

        panDeploy.add(labDeploymentRound, GBC.std());
        panDeploy.add(choDeploymentRound, GBC.eol());
        panDeploy.add(labDeploymentZone, GBC.std());
        panDeploy.add(choDeploymentZone, GBC.eol());
        if (clientgui.getClient().getGame().getOptions()
                .booleanOption(OptionsConstants.RPG_BEGIN_SHUTDOWN)
                && !(entity instanceof Infantry)
                && !(entity instanceof GunEmplacement)) {
            panDeploy.add(labDeployShutdown, GBC.std());
            panDeploy.add(chDeployShutdown, GBC.eol());
            chDeployShutdown.setSelected(entity.isManualShutdown());
        }

        if (isMech) {
            panDeploy.add(labDeployHullDown, GBC.std());
            panDeploy.add(chDeployHullDown, GBC.eol());
            chDeployHullDown.setSelected(entity.isHullDown()
                    && !entity.isProne());
            chDeployHullDown.addItemListener(this);

            panDeploy.add(labDeployProne, GBC.std());
            panDeploy.add(chDeployProne, GBC.eol());
            chDeployProne.setSelected(entity.isProne() && !entity.isHullDown());
            chDeployProne.addItemListener(this);
        }

        refreshDeployment();

        if (clientgui.getClient().getGame().getOptions()
                .booleanOption(OptionsConstants.ADVANCED_HIDDEN_UNITS)) {
            panDeploy.add(labHidden, GBC.std());
            panDeploy.add(chHidden, GBC.eol());
            chHidden.setSelected(entity.isHidden());
        }

        if (eligibleForOffBoard) {
            panDeploy.add(labOffBoard, GBC.std());
            panDeploy.add(chOffBoard, GBC.eol());
            chOffBoard.setSelected(entity.isOffBoard());

            panDeploy.add(labOffBoardDirection, GBC.std());

            choOffBoardDirection.addItem(Messages
                    .getString("CustomMechDialog.North")); //$NON-NLS-1$
            choOffBoardDirection.addItem(Messages
                    .getString("CustomMechDialog.South")); //$NON-NLS-1$
            choOffBoardDirection.addItem(Messages
                    .getString("CustomMechDialog.East")); //$NON-NLS-1$
            choOffBoardDirection.addItem(Messages
                    .getString("CustomMechDialog.West")); //$NON-NLS-1$
            direction = entity.getOffBoardDirection();
            if (OffBoardDirection.NONE == direction) {
                direction = OffBoardDirection.NORTH;
            }
            choOffBoardDirection.setSelectedIndex(direction.getValue());
            panDeploy.add(choOffBoardDirection, GBC.eol());

            panDeploy.add(labOffBoardDistance, GBC.std());

            butOffBoardDistance.addActionListener(this);
            butOffBoardDistance.setText(Integer.toString(distance));
            panDeploy.add(butOffBoardDistance, GBC.eol());
        }

        setupButtons();

        fldName.setText(entity.getCrew().getName());
        fldName.addActionListener(this);
        fldNick.setText(entity.getCrew().getNickname());
        fldNick.addActionListener(this);
        fldGunnery.setText(Integer.toString(entity.getCrew().getGunnery()));
        fldGunnery.addActionListener(this);
        fldGunneryL.setText(Integer.toString(entity.getCrew().getGunneryL()));
        fldGunneryL.addActionListener(this);
        fldGunneryM.setText(Integer.toString(entity.getCrew().getGunneryM()));
        fldGunneryM.addActionListener(this);
        fldGunneryB.setText(Integer.toString(entity.getCrew().getGunneryB()));
        fldGunneryB.addActionListener(this);
        fldPiloting.setText(Integer.toString(entity.getCrew().getPiloting()));
        fldPiloting.addActionListener(this);
        fldArtillery.setText(Integer.toString(entity.getCrew().getArtillery()));
        fldArtillery.addActionListener(this);
        fldFatigue.setText(Integer.toString(entity.getCrew().getFatigue()));
        fldFatigue.setToolTipText(Messages
                .getString("CustomMechDialog.labFatigueToolTip"));
        fldFatigue.addActionListener(this);
        fldTough.setText(Integer.toString(entity.getCrew().getToughness()));
        fldTough.addActionListener(this);
        fldInit.setText(Integer.toString(entity.getCrew().getInitBonus()));
        fldInit.addActionListener(this);
        fldCommandInit.setText(Integer.toString(entity.getCrew()
                .getCommandBonus()));
        fldCommandInit.addActionListener(this);
        if (isAero) {
            Aero a = (Aero) entity;
            fldStartVelocity.setText(new Integer(a.getCurrentVelocity())
                    .toString());
            fldStartVelocity.addActionListener(this);

            fldStartAltitude.setText(new Integer(a.getAltitude()).toString());
            fldStartAltitude.addActionListener(this);
        }
        if (isVTOL) {
            VTOL v = (VTOL) entity;
            fldStartHeight.setText(new Integer(v.getElevation()).toString());
            fldStartHeight.addActionListener(this);
        }

        if (!editable) {
            fldName.setEnabled(false);
            fldNick.setEnabled(false);
            fldGunnery.setEnabled(false);
            fldGunneryL.setEnabled(false);
            fldGunneryM.setEnabled(false);
            fldGunneryB.setEnabled(false);
            fldPiloting.setEnabled(false);
            fldArtillery.setEnabled(false);
            fldFatigue.setEnabled(false);
            fldTough.setEnabled(false);
            fldInit.setEnabled(false);
            fldCommandInit.setEnabled(false);
            choDeploymentRound.setEnabled(false);
            chDeployShutdown.setEnabled(false);
            chDeployProne.setEnabled(false);
            chDeployHullDown.setEnabled(false);
            chCommander.setEnabled(false);
            chHidden.setEnabled(false);
            chOffBoard.setEnabled(false);
            choOffBoardDirection.setEnabled(false);
            fldOffBoardDistance.setEnabled(false);
            fldStartVelocity.setEnabled(false);
            fldStartAltitude.setEnabled(false);
            fldStartHeight.setEnabled(false);
            m_equip.initialize();
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });

        pack();
        mainPanel.setSize(mainPanel.getSize().width,
                Math.min(mainPanel.getSize().height, 400));
        setResizable(true);
        setLocationRelativeTo(clientgui);
    }

    public int getSelectedTab() {
        return tabAll.getSelectedIndex();
    }

    public void setSelectedTab(int idx) {
        tabAll.setSelectedIndex(idx);
    }

    private void setupButtons() {
        butOkay.addActionListener(this);
        butCancel.addActionListener(this);
        butNext.addActionListener(this);
        butPrev.addActionListener(this);
        butRandomSkill.addActionListener(this);
        butRandomName.addActionListener(this);

        // layout
        panButtons.setLayout(new GridLayout(1, 4, 10, 0));
        panButtons.add(butPrev);
        panButtons.add(butOkay);
        panButtons.add(butCancel);
        panButtons.add(butNext);

        butNext.setEnabled(getNextEntity(true) != null);
        butPrev.setEnabled(getNextEntity(false) != null);
    }

    private void setOptions() {
        Entity entity = entities.get(0);
        IOption option;
        for (final Object newVar : optionComps) {
            DialogOptionComponent comp = (DialogOptionComponent) newVar;
            option = comp.getOption();
            if ((comp.getValue() == Messages.getString("CustomMechDialog.None"))) { // NON-NLS-$1
                entity.getCrew().getOptions().getOption(option.getName())
                        .setValue("None"); // NON-NLS-$1
            } else {
                entity.getCrew().getOptions().getOption(option.getName())
                        .setValue(comp.getValue());
            }
        }
    }

    public void refreshOptions() {
        panOptions.removeAll();
        optionComps = new ArrayList<DialogOptionComponent>();

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panOptions.setLayout(gridbag);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx = 0;
        c.ipady = 0;

        for (Enumeration<IOptionGroup> i = options.getGroups(); i
                .hasMoreElements();) {
            IOptionGroup group = i.nextElement();

            if (group.getKey().equalsIgnoreCase(PilotOptions.LVL3_ADVANTAGES)
                    && !clientgui.getClient().getGame().getOptions()
                            .booleanOption(OptionsConstants.RPG_PILOT_ADVANTAGES)) {
                continue;
            }

            if (group.getKey().equalsIgnoreCase(PilotOptions.EDGE_ADVANTAGES)
                    && !clientgui.getClient().getGame().getOptions()
                            .booleanOption(OptionsConstants.EDGE)) {
                continue;
            }

            if (group.getKey().equalsIgnoreCase(PilotOptions.MD_ADVANTAGES)
                    && !clientgui.getClient().getGame().getOptions()
                            .booleanOption(OptionsConstants.RPG_MANEI_DOMINI)) {
                continue;
            }

            addGroup(group, gridbag, c);

            Entity entity = entities.get(0);
            for (Enumeration<IOption> j = group.getOptions(); j
                    .hasMoreElements();) {
                IOption option = j.nextElement();

                if (entity instanceof GunEmplacement) {
                    continue;
                }

                // a bunch of stuf should get disabled for conv infantry
                if ((((entity instanceof Infantry) && !(entity instanceof BattleArmor)))
                        && (option.getName().equals(OptionsConstants.MD_VDNI) || option.getName()
                                .equals(OptionsConstants.MD_BVDNI))) {
                    continue;
                }

                // a bunch of stuff should get disabled for all but conventional
                // infantry
                if (!((entity instanceof Infantry) && !(entity instanceof BattleArmor))
                        && (option.getName().equals(OptionsConstants.MD_PL_ENHANCED)
                                || option.getName().equals(OptionsConstants.MD_PL_MASC)
                                || option.getName().equals(OptionsConstants.MD_CYBER_IMP_AUDIO) || option
                                .getName().equals(OptionsConstants.MD_CYBER_IMP_VISUAL))) {
                    continue;
                }

                addOption(option, gridbag, c, editable);
            }
        }

        validate();
    }

    private void setPartReps() {
        Entity entity = entities.get(0);
        IOption option;
        for (final Object newVar : partRepsComps) {
            DialogOptionComponent comp = (DialogOptionComponent) newVar;
            option = comp.getOption();
            if ((comp.getValue() == Messages.getString("CustomMechDialog.None"))) { // NON-NLS-$1
                entity.getPartialRepairs().getOption(option.getName())
                        .setValue("None"); // NON-NLS-$1
            } else {
                entity.getPartialRepairs().getOption(option.getName())
                        .setValue(comp.getValue());
            }
        }

    }

    private void setQuirks() {
        panQuirks.setQuirks();
    }

    public void refreshPartReps() {
        Entity entity = entities.get(0);
        panPartReps.removeAll();
        partRepsComps = new ArrayList<DialogOptionComponent>();
        for (Enumeration<IOptionGroup> i = partReps.getGroups(); i
                .hasMoreElements();) {
            IOptionGroup group = i.nextElement();
            panPartReps.add(new JLabel(group.getDisplayableName()), GBC.eol());

            for (Enumeration<IOption> j = group.getSortedOptions(); j
                    .hasMoreElements();) {
                IOption option = j.nextElement();

                if (!PartialRepairs.isPartRepLegalFor(option, entity)) {
                    continue;
                }

                addPartRep(option, editable);
            }
        }
        validate();
    }

    public void refreshQuirks() {
        panQuirks.refreshQuirks();
    }

    private void addGroup(IOptionGroup group, GridBagLayout gridbag,
            GridBagConstraints c) {
        JLabel groupLabel = new JLabel(group.getDisplayableName());

        gridbag.setConstraints(groupLabel, c);
        panOptions.add(groupLabel);
    }

    private void addOption(IOption option, GridBagLayout gridbag, GridBagConstraints c, boolean editable) {
        Entity entity = entities.get(0);
        DialogOptionComponent optionComp = new DialogOptionComponent(this, option, editable);

        if ((OptionsConstants.GUNNERY_WEAPON_SPECIALIST).equals(option.getName())) { // $NON-NLS-1$
            optionComp.addValue(Messages.getString("CustomMechDialog.None")); //$NON-NLS-1$
            TreeSet<String> uniqueWeapons = new TreeSet<String>();
            for (int i = 0; i < entity.getWeaponList().size(); i++) {
                Mounted m = entity.getWeaponList().get(i);
                uniqueWeapons.add(m.getName());
            }
            for (String name : uniqueWeapons) {
                optionComp.addValue(name);
            }
            optionComp.setSelected(option.stringValue());
        }

        if (OptionsConstants.GUNNERY_SPECIALIST               
                .equals(option.getName())) { //$NON-NLS-1$
            optionComp.addValue(Crew.SPECIAL_NONE);
            optionComp.addValue(Crew.SPECIAL_LASER);
            optionComp.addValue(Crew.SPECIAL_BALLISTIC);
            optionComp.addValue(Crew.SPECIAL_MISSILE);
            optionComp.setSelected(option.stringValue());
        }

        if (OptionsConstants.GUNNERY_RANGE_MASTER.equals(option.getName())) { //$NON-NLS-1$
            optionComp.addValue(Crew.RANGEMASTER_NONE);
            optionComp.addValue(Crew.RANGEMASTER_MEDIUM);
            optionComp.addValue(Crew.RANGEMASTER_LONG);
            optionComp.addValue(Crew.RANGEMASTER_EXTREME);
            optionComp.setSelected(option.stringValue());
        }

        gridbag.setConstraints(optionComp, c);
        panOptions.add(optionComp);

        optionComps.add(optionComp);
    }

    private void addPartRep(IOption option, boolean editable) {
        DialogOptionComponent optionComp = new DialogOptionComponent(this,
                option, editable);
        panPartReps.add(optionComp, GBC.eol());
        partRepsComps.add(optionComp);
    }

    public void optionClicked(DialogOptionComponent comp, IOption option,
            boolean state) {
    }

    public boolean isOkay() {
        return okay;
    }

    public int getStatus() {
        return status;
    }

    private void refreshDeployment() {
        choDeploymentRound.removeItemListener(this);
        
        choDeploymentRound.removeAllItems();
        choDeploymentRound.addItem(Messages
                .getString("CustomMechDialog.StartOfGame")); //$NON-NLS-1$

        Entity entity = entities.get(0);
        if (entity.getDeployRound() < 1) {
            choDeploymentRound.setSelectedIndex(0);
        }

        for (int i = 1; i <= 40; i++) {
            choDeploymentRound.addItem(Messages
                    .getString("CustomMechDialog.AfterRound") + i); //$NON-NLS-1$

            if (entity.getDeployRound() == i) {
                choDeploymentRound.setSelectedIndex(i);
            }
        }
        if (entity.getTransportId() != Entity.NONE) {
            choDeploymentRound.setEnabled(false);
        }
        
        choDeploymentZone.removeAllItems();
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.useOwners")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deployAny")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deployNW")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deployN")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deployNE")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deployE")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deploySE")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deployS")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deploySW")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deployW")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deployEdge")); //$NON-NLS-1$
        choDeploymentZone.addItem(Messages
                .getString("CustomMechDialog.deployCenter")); //$NON-NLS-1$
        
        if (entity.getDeployRound() == 0) {
            choDeploymentZone.setEnabled(false);
        } else {
            choDeploymentZone.setEnabled(true);
        }
        choDeploymentZone.setSelectedIndex(entity.getStartingPos(false) + 1);
        
        choDeploymentRound.addItemListener(this);

        chHidden.removeActionListener(this);
        boolean enableHidden = true;
        // Airborne units can't be hidden
        if (entity.isAirborne() || entity.isAirborneVTOLorWIGE()) {
            enableHidden = false;
        }
        // Landed dropships can't be hidden
        if ((entity instanceof Dropship)) {
            enableHidden = false;
        }
        labHidden.setEnabled(enableHidden);
        chHidden.setEnabled(enableHidden);
        chHidden.addActionListener(this);
    }

    /**
     * Populate the list of entities in other units from the given enumeration.
     *
     * @param others
     *            the <code>Enumeration</code> containing entities in other
     *            units.
     */
    private void refreshUnitNum(Iterator<Entity> others) {
        Entity entity = entities.get(0);
        // Clear the list of old values
        choUnitNum.removeAllItems();
        entityUnitNum.clear();

        // Make an entry for "no change".
        choUnitNum.addItem(Messages
                .getString("CustomMechDialog.doNotSwapUnits")); //$NON-NLS-1$
        entityUnitNum.add(entity);

        // Walk through the other entities.
        while (others.hasNext()) {
            // Track the position of the next other entity.
            final Entity other = others.next();
            entityUnitNum.add(other);

            // Show the other entity's name and callsign.
            StringBuffer callsign = new StringBuffer(other.getDisplayName());
            callsign.append(" (")//$NON-NLS-1$
                    .append((other.getUnitNumber() + PreferenceManager
                            .getClientPreferences().getUnitStartChar()))
                    .append('-').append(other.getId()).append(')');
            choUnitNum.addItem(callsign.toString());
        }
        choUnitNum.setSelectedIndex(0);
    }

    public void actionPerformed(ActionEvent actionEvent) {

        if (actionEvent.getSource().equals(butRandomSkill)) {
            int[] skills = client.getRandomSkillsGenerator().getRandomSkills(
                    entities.get(0));
            fldGunnery.setText(Integer.toString(skills[0]));
            fldPiloting.setText(Integer.toString(skills[1]));
            return;
        }
        if (actionEvent.getSource().equals(butRandomName)) {
            fldName.setText(client.getRandomNameGenerator().generate());
            return;
        }
        if (actionEvent.getSource().equals(butOffBoardDistance)) {
            int maxDistance = 19 * 17; // Long Tom
            for (Entity entity : entities) {
                for (Mounted wep : entity.getWeaponList()) {
                    EquipmentType e = wep.getType();
                    WeaponType w = (WeaponType) e;
                    if (w.hasFlag(WeaponType.F_ARTILLERY)) {
                        int nDistance = (w.getLongRange() - 1) * 17;
                        if (nDistance < maxDistance) {
                            maxDistance = nDistance;
                        }
                    }
                }
            }
            // int dist = Math.min(Math.max(entity.getOffBoardDistance(), 17),
            // maxDistance);
            Slider sl = new Slider(
                    clientgui.frame,
                    Messages.getString("CustomMechDialog.offboardDistanceTitle"),
                    Messages.getString("CustomMechDialog.offboardDistanceQuestion"),
                    Math.min(
                            Math.max(entities.get(0).getOffBoardDistance(), 17),
                            maxDistance), 17, maxDistance);
            if (!sl.showDialog()) {
                return;
            }
            distance = sl.getValue();
            butOffBoardDistance.setText(Integer.toString(distance));
            return;
        }

        if (actionEvent.getSource().equals(chHidden)) {
            return;
        }

        if (actionEvent.getSource().equals(butCancel)) {
            setVisible(false);
            return;
        }

        // Set instanceof flags
        String msg, title;
        boolean isAero = true;
        boolean isVTOL = true;
        for (Entity e : entities) {
            isAero &= e instanceof Aero;
            isVTOL &= e instanceof VTOL;
        }

        // get values
        String name = fldName.getText();
        String nick = fldNick.getText();
        int gunnery;
        int gunneryL;
        int gunneryM;
        int gunneryB;
        int artillery;
        int fatigue = 0;
        int piloting;
        int tough = 0;
        int init = 0;
        int command = 0;
        int velocity = 0;
        int altitude = 0;
        int height = 0;
        int offBoardDistance;
        String externalId = entities.get(0).getCrew().getExternalIdAsString();
        try {
            gunnery = Integer.parseInt(fldGunnery.getText());
            gunneryL = Integer.parseInt(fldGunneryL.getText());
            gunneryM = Integer.parseInt(fldGunneryM.getText());
            gunneryB = Integer.parseInt(fldGunneryB.getText());
            piloting = Integer.parseInt(fldPiloting.getText());
            artillery = Integer.parseInt(fldArtillery.getText());
            tough = Integer.parseInt(fldTough.getText());
            init = Integer.parseInt(fldInit.getText());
            fatigue = Integer.parseInt(fldFatigue.getText());
            command = Integer.parseInt(fldCommandInit.getText());
            if (isAero) {
                velocity = Integer.parseInt(fldStartVelocity.getText());
                altitude = Integer.parseInt(fldStartAltitude.getText());
            }
            if (isVTOL) {
                height = Integer.parseInt(fldStartHeight.getText());
            }
        } catch (NumberFormatException e) {
            msg = Messages.getString("CustomMechDialog.EnterValidSkills"); //$NON-NLS-1$
            title = Messages.getString("CustomMechDialog.NumberFormatError"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(clientgui.frame, msg, title,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // keep these reasonable, please
        if ((gunnery < 0) || (gunnery > 8) || (piloting < 0) || (piloting > 8)
                || (gunneryL < 0) || (gunneryL > 8) || (gunneryM < 0)
                || (gunneryM > 8) || (gunneryB < 0) || (gunneryB > 8)
                || (artillery < 0) || (artillery > 8)) {
            msg = Messages.getString("CustomMechDialog.EnterSkillsBetween0_8"); //$NON-NLS-1$
            title = Messages.getString("CustomMechDialog.NumberFormatError"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(clientgui.frame, msg, title,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isAero) {
            if ((velocity > (2 * entities.get(0).getWalkMP()))
                    || (velocity < 0)) {
                msg = Messages
                        .getString("CustomMechDialog.EnterCorrectVelocity"); //$NON-NLS-1$
                title = Messages
                        .getString("CustomMechDialog.NumberFormatError"); //$NON-NLS-1$
                JOptionPane.showMessageDialog(clientgui.frame, msg, title,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ((altitude < 0) || (altitude > 10)) {
                msg = Messages
                        .getString("CustomMechDialog.EnterCorrectAltitude"); //$NON-NLS-1$
                title = Messages
                        .getString("CustomMechDialog.NumberFormatError"); //$NON-NLS-1$
                JOptionPane.showMessageDialog(clientgui.frame, msg, title,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (isVTOL && (height > 50)) {
            msg = Messages.getString("CustomMechDialog.EnterCorrectHeight"); //$NON-NLS-1$
            title = Messages.getString("CustomMechDialog.NumberFormatError"); //$NON-NLS-1$
            JOptionPane.showMessageDialog(clientgui.frame, msg, title,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Apply single-entity settings
        if (entities.size() == 1) {
            Entity entity = entities.get(0);

            if (client.getGame().getOptions().booleanOption(OptionsConstants.RPG_RPG_GUNNERY)) {
                entity.setCrew(new Crew(name, Compute.getFullCrewSize(entity),
                        gunneryL, gunneryM, gunneryB, piloting));
            } else {
                entity.setCrew(new Crew(name, Compute.getFullCrewSize(entity),
                        gunnery, piloting));
            }
            entity.getCrew().setArtillery(artillery);
            entity.getCrew().setFatigue(fatigue);
            entity.getCrew().setToughness(tough);
            entity.getCrew().setInitBonus(init);
            entity.getCrew().setCommandBonus(command);
            entity.getCrew().setNickname(nick);
            entity.getCrew().setPortraitCategory(portraitDialog.getCategory());
            entity.getCrew().setPortraitFileName(portraitDialog.getFileName());
            entity.getCrew().setExternalIdAsString(externalId);

            // If the player wants to swap unit numbers, update both
            // entities and send an update packet for the other entity.
            if (!entityUnitNum.isEmpty() && (choUnitNum.getSelectedIndex() > 0)) {
                Entity other = entityUnitNum.get(choUnitNum.getSelectedIndex());
                short temp = entity.getUnitNumber();
                entity.setUnitNumber(other.getUnitNumber());
                other.setUnitNumber(temp);
                client.sendUpdateEntity(other);
            }

            // update commander status
            entity.setCommander(chCommander.isSelected());

            setOptions();
            setQuirks();
            setPartReps();
            m_equip.applyChoices();

            if (entity instanceof BattleArmor) {
                // have to reset internals because of dermal armor option
                if (entity.getCrew().getOptions().booleanOption(OptionsConstants.MD_DERMAL_ARMOR)) {
                    ((BattleArmor) entity).setInternal(2);
                } else {
                    ((BattleArmor) entity).setInternal(1);
                }
            } else if (entity instanceof Infantry) {
                // need to reset armor on conventional infantry
                if (entity.getCrew().getOptions().booleanOption(OptionsConstants.MD_DERMAL_ARMOR)) {
                    entity.initializeArmor(
                            entity.getOInternal(Infantry.LOC_INFANTRY),
                            Infantry.LOC_INFANTRY);
                } else {
                    entity.initializeArmor(0, Infantry.LOC_INFANTRY);
                }
            }
        }

        // Apply multiple-entity settings
        for (Entity entity : entities) {
            entity.setHidden(chHidden.isSelected());

            if (chOffBoard.isSelected()) {
                try {
                    offBoardDistance = distance;
                } catch (NumberFormatException e) {
                    msg = Messages
                            .getString("CustomMechDialog.EnterValidSkills"); //$NON-NLS-1$
                    title = Messages
                            .getString("CustomMechDialog.NumberFormatError"); //$NON-NLS-1$
                    JOptionPane.showMessageDialog(clientgui.frame, msg, title,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (offBoardDistance < 17) {
                    msg = Messages
                            .getString("CustomMechDialog.OffboardDistance"); //$NON-NLS-1$
                    title = Messages
                            .getString("CustomMechDialog.NumberFormatError"); //$NON-NLS-1$
                    JOptionPane.showMessageDialog(clientgui.frame, msg, title,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                entity.setOffBoard(offBoardDistance, OffBoardDirection
                        .getDirection(choOffBoardDirection.getSelectedIndex()));
            } else {
                entity.setOffBoard(0, OffBoardDirection.NONE);
            }

            if (isAero) {
                Aero a = (Aero) entity;
                a.setCurrentVelocity(velocity);
                a.setNextVelocity(velocity);
                // we need to determine whether this aero is airborne or not in
                // order for prohibited terrain and stacking to work right in
                // the
                // deployment phase this is very tricky because in atmosphere,
                // zero
                // altitude does not necessarily mean grounded
                if (altitude <= 0) {
                    a.land();
                } else {
                    a.liftOff(altitude);
                }
            }

            if (isVTOL) {
                VTOL v = (VTOL) entity;
                v.setElevation(height);
            }

            // Set the entity's deployment position and round.
            entity.setStartingPos(choDeploymentZone.getSelectedIndex() - 1);
            entity.setDeployRound(choDeploymentRound.getSelectedIndex());

            // Should the entity begin the game shutdown?
            if (chDeployShutdown.isSelected()
                    && clientgui.getClient().getGame().getOptions()
                            .booleanOption(OptionsConstants.RPG_BEGIN_SHUTDOWN)) {
                entity.performManualShutdown();
            } else { // We need to else this in case someone turned the option
                     // on, set their units, and then turned the option off.
                entity.performManualStartup();
            }

            // Should the entity begin the game prone?
            entity.setProne(chDeployProne.isSelected());

            // Should the entity begin the game prone?
            entity.setHullDown(chDeployHullDown.isSelected());
        }

        okay = true;
        status = DONE;
        clientgui.chatlounge.refreshEntities();

        // Check validity of units after customization
        for (Entity entity : entities) {
            EntityVerifier verifier = EntityVerifier.getInstance(new File(
                    Configuration.unitsDir(), EntityVerifier.CONFIG_FILENAME));
            TestEntity testEntity = null;
            if (entity instanceof Mech) {
                testEntity = new TestMech((Mech) entity, verifier.mechOption,
                        null);
            } else if ((entity instanceof Tank)
                    && !(entity instanceof GunEmplacement)) {
                if (entity.isSupportVehicle()) {
                    testEntity = new TestSupportVehicle((Tank) entity,
                            verifier.tankOption, null);
                } else {
                    testEntity = new TestTank((Tank) entity,
                            verifier.tankOption, null);
                }
            } else if (entity.getEntityType() == Entity.ETYPE_AERO
                    && entity.getEntityType() != Entity.ETYPE_DROPSHIP
                    && entity.getEntityType() != Entity.ETYPE_SMALL_CRAFT
                    && entity.getEntityType() != Entity.ETYPE_FIGHTER_SQUADRON
                    && entity.getEntityType() != Entity.ETYPE_JUMPSHIP
                    && entity.getEntityType() != Entity.ETYPE_SPACE_STATION) {
                testEntity = new TestAero((Aero) entity, verifier.mechOption,
                        null);
            } else if (entity instanceof BattleArmor) {
                testEntity = new TestBattleArmor((BattleArmor) entity,
                        verifier.baOption, null);
            } else if (entity instanceof Infantry) {
                testEntity = new TestInfantry((Infantry) entity,
                        verifier.infOption, null);
            }
            int gameTL = TechConstants.getGameTechLevel(client.getGame(),
                    entity.isClan());
            if ((testEntity != null)
                    && !testEntity.correctEntity(new StringBuffer(), gameTL)) {
                entity.setDesignValid(false);
            } else {
                entity.setDesignValid(true);
            }
        }

        if (actionEvent.getSource().equals(butPrev)) {
            status = PREV;
        } else if (actionEvent.getSource().equals(butNext)) {
            status = NEXT;
        }
        setVisible(false);
    }

    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getSource().equals(chDeployProne)) {
            chDeployHullDown.setSelected(false);
            return;
        }
        if (itemEvent.getSource().equals(chDeployHullDown)) {
            chDeployProne.setSelected(false);
            return;
        }
        if (itemEvent.getSource().equals(choDeploymentRound)) {
            if (choDeploymentRound.getSelectedIndex() == 0) {
                choDeploymentZone.setEnabled(false);
                choDeploymentZone.setSelectedIndex(0);
            } else {
                choDeploymentZone.setEnabled(true);
            }
        }
    }

    public Entity getNextEntity(boolean forward) {
        IGame game = client.getGame();
        boolean bd = game.getOptions().booleanOption(OptionsConstants.BASE_BLIND_DROP); //$NON-NLS-1$
        boolean rbd = game.getOptions().booleanOption(OptionsConstants.BASE_REAL_BLIND_DROP); //$NON-NLS-1$
        IPlayer p = client.getLocalPlayer();

        Entity nextOne;
        Entity entity;
        if (forward) {
            entity = entities.get(entities.size() - 1);
            nextOne = game.getNextEntityFromList(entity);
        } else {
            entity = entities.get(0);
            nextOne = game.getPreviousEntityFromList(entity);
        }
        while ((nextOne != null) && !entities.contains(nextOne)) {
            if (nextOne.getOwner().equals(p)
                    || (!(bd || rbd) && nextOne.getOwner().equals(
                            entity.getOwner()))) {
                return nextOne;
            }
            if (forward) {
                nextOne = game.getNextEntityFromList(nextOne);
            } else {
                nextOne = game.getPreviousEntityFromList(nextOne);
            }
        }
        return null;
    }

    private void setupEquip() {
        Entity entity = entities.get(0);
        GridBagLayout gbl = new GridBagLayout();
        panEquip.setLayout(gbl);

        m_equip = new EquipChoicePanel(entity, clientgui, client);
        panEquip.add(m_equip, GBC.std());
    }
}
