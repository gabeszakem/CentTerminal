/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal.frame;

import centterminal.CentTerminal;
import static centterminal.CentTerminal.actRecord;
import centterminal.record.Record;
import centterminal.tools.ActualDate;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author gkovacs02
 */
public class CentterminalFrame extends javax.swing.JFrame {

    private String NodeAddress;
    @SuppressWarnings("FieldMayBeFinal")
    private HashMap<Integer, String> codeTextCacheMap;
    private int code;
    private String codeName;
    private static final Color BUTTONDEFAULTCOLOR = new java.awt.Color(238, 238, 238);
    public final Color communicationFaultColor = new java.awt.Color(255, 0, 0);
    public final Color communicationOkColor = new java.awt.Color(0, 204, 0);
    private static boolean beepWorking = true;
    private static int modeChangeCount = 0;
    private CentTerminalAboutBox centTerminalAboutBox;
    private static boolean communicationOk = true;

    /**
     * A terminál ablak
     */
    public CentterminalFrame() {
        /**
         * Inicializálás
         */
        initComponents();
        
        serverPanel.setMinimumSize(new java.awt.Dimension(10, 10));
        serverPanel.setPreferredSize(new java.awt.Dimension(10, 10));
        serverPanel.setLayout(new java.awt.GridBagLayout());
        
        plcPanel.setMinimumSize(new java.awt.Dimension(10, 10));
        plcPanel.setPreferredSize(new java.awt.Dimension(10, 10));
        plcPanel.setLayout(new java.awt.GridBagLayout());
        
        dbPanel.setMinimumSize(new java.awt.Dimension(10, 10));
        dbPanel.setPreferredSize(new java.awt.Dimension(10, 10));
        dbPanel.setLayout(new java.awt.GridBagLayout());
        
        /**
         * Ikon beállítása
         */
        setIconImage(Toolkit.getDefaultToolkit().getImage(CentterminalFrame.class.getResource("/centterminal/images/icon_256px.png")));

        /**
         * Ablak középre állítása
         */
        this.setLocationRelativeTo(null);

        /**
         * Csak egy node-ot lehessen kijelölni
         */
        this.treeCodeSelector.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        /**
         * Kódok lekérése adatbázisból
         */
        codeTextCacheMap = CentTerminal.sql.getCodes(CentTerminal.node.node_addr);
        /**
         * Fa struktura feltöltése
         */
        this.fillTree(treeCodeSelector, 0);
        /**
         * mode
         */
        this.mode();

    }

    /**
     * Álláskód-faábra feltöltése.
     *
     * @param aTree Az adott ablak JTree objektuma.
     * @param aExpandAtCode Az itt meghatározott kód-nál nyissa szét a listát.
     */
    @SuppressWarnings("SuspiciousIndentAfterControlStatement")
    private void fillTree(JTree aTree, int aExpandAtCode) {
        // Fastruktúra feltöltése
        DefaultTreeModel dtm = (DefaultTreeModel) aTree.getModel();

        // Fa törlése
        aTree.removeAll();
        // Gyökér node létrehozása
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root", true);

        // Fa struktúra felépítése
        dtm.setRoot(rootNode);

        DefaultMutableTreeNode level1Node;  // 1000-el osztható kódok
        DefaultMutableTreeNode level2Node;  // 100-zal osztható kódok
        DefaultMutableTreeNode level3Node;  // 10-zel osztható kódok
        DefaultMutableTreeNode level4Node;  // Tovább nem pontosítható kódok
        DefaultMutableTreeNode nodeToAdd;   // A legutolsó hozzáadandó node
        TreePath treePathForDefinedNode = new TreePath(rootNode.getPath());    // A megadott kódhoz tartozó, szétnyitandó node útvonala
        boolean treeError = false;          // Hiba a struktúrában

        // A visszakapott állás kódokat és szövegeket tartalmazó MapSet<Integer, String> végigjárása
        // A map-ből nem lehet egyértelműen kiolvasni a kulcsokat, ezért kiolvassuk őket egy Set<Integer>-be
        // Persze a Set<Integer> véletlenül sincs abban a sorrendben, mint a MapSet-ben, ezért rendezni kell
        // Természetesen Set-et nem lehet rendezni csak List-et,
        // ezért List<Integer>-t kell belőle csinálni, mert csak azt lehet rendezni
        // vicc kategória...
        Set<Integer> keySet = codeTextCacheMap.keySet();
        ArrayList<Integer> intList = new ArrayList<>(keySet);
        Collections.sort(intList);
        Iterator<Integer> keySetIterator = intList.iterator();
        Integer keyValue = 0;

        // A fába 'ABCD. szöveg' formátumban kerülnek be az adatok (pl. '1000. Gépész')
        while (keySetIterator.hasNext() && !treeError) {
            keyValue = keySetIterator.next();
            nodeToAdd = new DefaultMutableTreeNode(keyValue.toString() + ". " + codeTextCacheMap.get(keyValue), true);

            // Level 1 node?
            if (keyValue % 1000 == 0) {
                rootNode.add(nodeToAdd);
            } else // Level 2 node?
            if (keyValue % 100 == 0) {
                // Az új node-ot a megfelelő helyre kell hozzáadni, ezért megkeressük a megfelelő szülőt
                level2Node = nodeToAdd;
                level1Node = this.getLevelNNodeFor(1, rootNode, level2Node);
                if (level1Node != null) {
                    level1Node.add(level2Node);
                } else {
                    treeError = true;
                }
            } else // Level 3 node?
            if (keyValue % 10 == 0) {
                level3Node = nodeToAdd;
                level1Node = this.getLevelNNodeFor(1, rootNode, level3Node);
                if (level1Node != null) {
                    level2Node = this.getLevelNNodeFor(2, level1Node, level3Node);
                    if (level1Node != null) {
                        level2Node.add(level3Node);
                    } else {
                        treeError = true;
                    }
                } else {
                    treeError = true;
                }

            } else {

                // Level 4 node! :)
                level4Node = nodeToAdd;

                // Elképzelhető, hogy rendszer kód (9001, 9002) - ezeknél nem kell ellenőrzéseket végrehajtani!
                if ((keyValue == 9001) || (keyValue == 9002)) {
                    /**
                     * A terminal programban nincs szükség 9001 illetve 9002
                     * kódra
                     */
                    //rootNode.add(level4Node);
                } else {
                    level1Node = this.getLevelNNodeFor(1, rootNode, level4Node);
                    if (level1Node != null) {
                        level2Node = this.getLevelNNodeFor(2, level1Node, level4Node);
                        if (level2Node != null) {
                            level3Node = this.getLevelNNodeFor(3, level2Node, level4Node);
                            if (level3Node != null) {
                                level3Node.add(level4Node);
                            } else {
                                treeError = true;
                            }
                        } else {
                            treeError = true;
                        }
                    } else {
                        treeError = true;
                    }
                }
            }

            // A szétnyitás helyének meghatározása
            if ((aExpandAtCode != 0) && (keyValue == aExpandAtCode)) {
                treePathForDefinedNode = new TreePath(nodeToAdd.getPath());
            }
        }

        // A gyökér node-ot (ami egyébként nem is látszik...) szét kell nyitni, különben
        // nem látszik semmi a faábrában...
        aTree.expandPath(new TreePath(rootNode.getPath()));

        // Ha nincs hiba, és meg lett adva a szétnyitás helye, akkor szétnyitjuk a listát a megadott helyen,
        // és kijelöljük az álláskódot
        if ((aExpandAtCode != 0) && !treeError) {
            aTree.expandPath(treePathForDefinedNode.getParentPath());
            aTree.setSelectionPath(treePathForDefinedNode);
        }
    }

    /**
     * n. szintű node keresése egy node számára
     *
     * @param aNodeLevel A szint értéke (min. 1, max. 3; a többi értéknek nincs
     * értelme az állás kód szempontjából).
     * @param aRootNode A gyökér node objektum.
     * @param aNode A node objektum, amely számára az n. szintű node-ot
     * keressük.
     * @return Az n. szintű node objektum, ha létezik, vagy <i>null</i> ha nem
     * létezik.
     */
    private DefaultMutableTreeNode getLevelNNodeFor(Integer aNodeLevel, DefaultMutableTreeNode aRootNode, DefaultMutableTreeNode aNode) {
        DefaultMutableTreeNode retLevelNNode = null;

        // Szint érték vizsgálat
        if ((aNodeLevel < 1) || (aNodeLevel > 3)) {
            return retLevelNNode;
        }

        // A node megállapítás az első n db. karakter egyezősége alapján történik
        String firstDigitOfParent = "";
        String firstDigitOfNode = aNode.getUserObject().toString().substring(0, aNodeLevel);
        boolean parentFound = false;
        int i = -1;

        while (!parentFound && (++i < aRootNode.getChildCount())) {
            firstDigitOfParent = ((DefaultMutableTreeNode) aRootNode.getChildAt(i)).getUserObject().toString().substring(0, aNodeLevel);
            parentFound = firstDigitOfNode.equals(firstDigitOfParent);
        }

        if (parentFound) {
            retLevelNNode = ((DefaultMutableTreeNode) aRootNode.getChildAt(i));
        }

        return retLevelNNode;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jButtonSetDownTimeCode = new javax.swing.JButton();
        jButtonModifyDownTimeCode = new javax.swing.JButton();
        jButtonShareDownTime = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        downTimeEnd = new javax.swing.JLabel();
        downTimeBegin = new javax.swing.JLabel();
        machineState = new javax.swing.JLabel();
        codeText = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeCodeSelector = new javax.swing.JTree();
        jPanel3 = new javax.swing.JPanel();
        jButtonProduction = new javax.swing.JButton();
        jButtonOutage = new javax.swing.JButton();
        jButtonMaintanence = new javax.swing.JButton();
        jButtonAboutBox = new javax.swing.JButton();
        plcLabel = new javax.swing.JLabel();
        plcPanel = new javax.swing.JPanel();
        dbLabel = new javax.swing.JLabel();
        dbPanel = new javax.swing.JPanel();
        serverLabel = new javax.swing.JLabel();
        serverPanel = new javax.swing.JPanel();
        commLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Állásidő Terminál - berendezés: "+CentTerminal.node.node_name +" - verzió: "+ CentTerminal.VERSION);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel1.setMinimumSize(new java.awt.Dimension(537, 65));
        jPanel1.setName(""); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jButtonSetDownTimeCode.setText("Állásidő megadása");
        jButtonSetDownTimeCode.setEnabled(false);
        jButtonSetDownTimeCode.setMaximumSize(new java.awt.Dimension(125, 23));
        jButtonSetDownTimeCode.setMinimumSize(new java.awt.Dimension(125, 23));
        jButtonSetDownTimeCode.setPreferredSize(new java.awt.Dimension(160, 50));
        jButtonSetDownTimeCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetDownTimeCodeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        jPanel1.add(jButtonSetDownTimeCode, gridBagConstraints);

        jButtonModifyDownTimeCode.setText("Állásidő módosítása");
        jButtonModifyDownTimeCode.setEnabled(false);
        jButtonModifyDownTimeCode.setPreferredSize(new java.awt.Dimension(160, 50));
        jButtonModifyDownTimeCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModifyDownTimeCodeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        jPanel1.add(jButtonModifyDownTimeCode, gridBagConstraints);

        jButtonShareDownTime.setText("Állásidő megosztása");
        jButtonShareDownTime.setToolTipText("");
        jButtonShareDownTime.setEnabled(false);
        jButtonShareDownTime.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jButtonShareDownTime.setMaximumSize(new java.awt.Dimension(125, 23));
        jButtonShareDownTime.setMinimumSize(new java.awt.Dimension(125, 23));
        jButtonShareDownTime.setPreferredSize(new java.awt.Dimension(160, 50));
        jButtonShareDownTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShareDownTimeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel1.add(jButtonShareDownTime, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        downTimeEnd.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        downTimeEnd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        downTimeEnd.setText("Leállás vége");
        downTimeEnd.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        downTimeEnd.setEnabled(false);
        downTimeEnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        downTimeEnd.setMaximumSize(new java.awt.Dimension(127, 32));
        downTimeEnd.setMinimumSize(new java.awt.Dimension(127, 32));
        downTimeEnd.setPreferredSize(new java.awt.Dimension(158, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel2.add(downTimeEnd, gridBagConstraints);

        downTimeBegin.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        downTimeBegin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        downTimeBegin.setText("Leállás kezdete");
        downTimeBegin.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        downTimeBegin.setEnabled(false);
        downTimeBegin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        downTimeBegin.setMaximumSize(new java.awt.Dimension(158, 32));
        downTimeBegin.setMinimumSize(new java.awt.Dimension(158, 32));
        downTimeBegin.setPreferredSize(new java.awt.Dimension(158, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        jPanel2.add(downTimeBegin, gridBagConstraints);

        machineState.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        machineState.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        machineState.setText("text");
        machineState.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        machineState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.9;
        jPanel2.add(machineState, gridBagConstraints);

        codeText.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        codeText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        codeText.setText("Kód");
        codeText.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        codeText.setEnabled(false);
        codeText.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        codeText.setMaximumSize(new java.awt.Dimension(41, 32));
        codeText.setMinimumSize(new java.awt.Dimension(41, 32));
        codeText.setPreferredSize(new java.awt.Dimension(41, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(codeText, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(jPanel2, gridBagConstraints);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treeCodeSelector.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeCodeSelector.setEnabled(false);
        treeCodeSelector.setName("treeCodeSelector"); // NOI18N
        treeCodeSelector.setRootVisible(false);
        treeCodeSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treeCodeSelectorMouseReleased(evt);
            }
        });
        treeCodeSelector.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeCodeSelectorValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(treeCodeSelector);
        treeCodeSelector.getAccessibleContext().setAccessibleName("");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));
        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel3.setMinimumSize(new java.awt.Dimension(221, 65));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButtonProduction.setText("Termelés");
        jButtonProduction.setToolTipText("");
        jButtonProduction.setEnabled(false);
        jButtonProduction.setMaximumSize(new java.awt.Dimension(125, 23));
        jButtonProduction.setMinimumSize(new java.awt.Dimension(125, 23));
        jButtonProduction.setPreferredSize(new java.awt.Dimension(160, 50));
        jButtonProduction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProductionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        jPanel3.add(jButtonProduction, gridBagConstraints);

        jButtonOutage.setText("Üzemszünet");
        jButtonOutage.setEnabled(false);
        jButtonOutage.setMaximumSize(new java.awt.Dimension(125, 23));
        jButtonOutage.setMinimumSize(new java.awt.Dimension(125, 23));
        jButtonOutage.setPreferredSize(new java.awt.Dimension(160, 50));
        jButtonOutage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOutageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        jPanel3.add(jButtonOutage, gridBagConstraints);

        jButtonMaintanence.setText("TMK");
        jButtonMaintanence.setEnabled(false);
        jButtonMaintanence.setMaximumSize(new java.awt.Dimension(125, 23));
        jButtonMaintanence.setMinimumSize(new java.awt.Dimension(125, 23));
        jButtonMaintanence.setPreferredSize(new java.awt.Dimension(160, 50));
        jButtonMaintanence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMaintanenceActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel3.add(jButtonMaintanence, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        getContentPane().add(jPanel3, gridBagConstraints);

        jButtonAboutBox.setBackground(BUTTONDEFAULTCOLOR);
        jButtonAboutBox.setText("Névjegy");
        jButtonAboutBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAboutBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 10);
        getContentPane().add(jButtonAboutBox, gridBagConstraints);
        jButtonAboutBox.getAccessibleContext().setAccessibleName("jButtonAboutBox");
        jButtonAboutBox.getAccessibleContext().setAccessibleDescription("");

        plcLabel.setText("PLC");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        getContentPane().add(plcLabel, gridBagConstraints);

        plcPanel.setBackground(new java.awt.Color(255, 0, 0));
        plcPanel.setForeground(new java.awt.Color(255, 0, 0));
        plcPanel.setMinimumSize(new java.awt.Dimension(10, 10));
        plcPanel.setName(""); // NOI18N
        plcPanel.setPreferredSize(new java.awt.Dimension(10, 10));
        plcPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        getContentPane().add(plcPanel, gridBagConstraints);

        dbLabel.setText("Adatbázis");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        getContentPane().add(dbLabel, gridBagConstraints);

        dbPanel.setBackground(new java.awt.Color(0, 204, 0));
        dbPanel.setForeground(new java.awt.Color(255, 0, 0));
        dbPanel.setToolTipText("");
        dbPanel.setMinimumSize(new java.awt.Dimension(10, 10));
        dbPanel.setName(""); // NOI18N
        dbPanel.setPreferredSize(new java.awt.Dimension(10, 10));
        dbPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        getContentPane().add(dbPanel, gridBagConstraints);

        serverLabel.setText("Állásidő szerver");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        getContentPane().add(serverLabel, gridBagConstraints);

        serverPanel.setBackground(new java.awt.Color(0, 204, 0));
        serverPanel.setForeground(new java.awt.Color(255, 0, 0));
        serverPanel.setToolTipText("");
        serverPanel.setMinimumSize(new java.awt.Dimension(10, 10));
        serverPanel.setName(""); // NOI18N
        serverPanel.setPreferredSize(new java.awt.Dimension(10, 10));
        serverPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        getContentPane().add(serverPanel, gridBagConstraints);

        commLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        commLabel.setText("Kommunikáció:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 30);
        getContentPane().add(commLabel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void treeCodeSelectorValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeCodeSelectorValueChanged
        ////
    }//GEN-LAST:event_treeCodeSelectorValueChanged
//**Egér esemény az állásidőkód fában*/

    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    private void treeCodeSelectorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeCodeSelectorMouseReleased
        try {
            String selectedCode = treeCodeSelector.getPathForLocation(evt.getX(), evt.getY()).getLastPathComponent().toString();
            String[] codeAndName = selectedCode.split(". ");
            code = Integer.parseInt(codeAndName[0]);
            codeName = codeAndName[1];
            for (int i = 2; i < codeAndName.length; i++) {
                codeName += " " + codeAndName[i];
            }
            int lastCode = -1;
            if (CentTerminal.pLCSignals.plantStatus == 0) {
                lastCode = CentTerminal.actRecord.code;
            } else if (!CentTerminal.records.isEmpty()) {
                lastCode = CentTerminal.records.getLast().code;
            }

            if (lastCode == 0 && treeCodeSelector.getModel().isLeaf((DefaultMutableTreeNode) treeCodeSelector.getLastSelectedPathComponent())) {
                jButtonSetDownTimeCode.setEnabled(true);
            } else {
                jButtonSetDownTimeCode.setEnabled(false);
            }
            if (lastCode != code && lastCode > 0 && treeCodeSelector.getModel().isLeaf((DefaultMutableTreeNode) treeCodeSelector.getLastSelectedPathComponent())) {
                if (CentTerminal.pLCSignals.plantStatus == 0) {
                    jButtonShareDownTime.setEnabled(true);
                    jButtonModifyDownTimeCode.setEnabled(true);
                }

            } else {
                jButtonModifyDownTimeCode.setEnabled(false);
                jButtonShareDownTime.setEnabled(false);
            }

        } catch (Exception ex) {
            // ex.printStackTrace(System.err);
        }
    }//GEN-LAST:event_treeCodeSelectorMouseReleased
    /**
     * Állásidő kód megadása
     */
    private void jButtonSetDownTimeCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetDownTimeCodeActionPerformed
        System.out.println("A kiválasztott kód: " + Integer.toString(code) + "\nA kiválasztott kód megnevezése: " + codeName);
        jButtonSetDownTimeCode.setEnabled(false);
        if (CentTerminal.pLCSignals.plantStatus == 0) {
            CentTerminal.actRecord.code = code;
        } else if (!CentTerminal.records.isEmpty()) {
            CentTerminal.records.getLast().code = code;
        }
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "jButtonSetDownTimeCodeActionPerformed(evt) : Állásidő megadása nyomógomb megnyomva");
    }//GEN-LAST:event_jButtonSetDownTimeCodeActionPerformed
    /**
     * Állásidő kód módosítása
     */
    private void jButtonModifyDownTimeCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModifyDownTimeCodeActionPerformed
        System.out.println("Állásidő módosítás: " + Integer.toString(code) + "\nA kiválasztott kód megnevezése: " + codeName);

        jButtonModifyDownTimeCode.setEnabled(false);
        jButtonShareDownTime.setEnabled(false);

        if (CentTerminal.pLCSignals.plantStatus == 0) {
            CentTerminal.actRecord.code = code;
        }
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "jButtonModifyDownTimeCodeActionPerformed(evt) : Állásidő módosítás nyomógomb megnyomva");
    }//GEN-LAST:event_jButtonModifyDownTimeCodeActionPerformed
    /**
     * Állásidőkód megosztása
     */
    private void jButtonShareDownTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShareDownTimeActionPerformed
        System.out.println("Állásidő megosztás: " + Integer.toString(code) + "\nA kiválasztott kód megnevezése: " + codeName);
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "jButtonShareDownTimeActionPerformed(evt) : Állásidő megosztás nyomógomb megnyomva");
        jButtonModifyDownTimeCode.setEnabled(false);
        jButtonShareDownTime.setEnabled(false);
        if (CentTerminal.pLCSignals.plantStatus == 0) {
            this.shareRecord(code);
        }
    }//GEN-LAST:event_jButtonShareDownTimeActionPerformed
    /**
     * Üzemmód választás : Termelés
     */
    private void jButtonProductionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProductionActionPerformed
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "jButtonProductionActionPerformed(evt) : Termelés nyomógomb megnyomva");
        CentTerminal.mode = 1;
        if (CentTerminal.pLCSignals.plantStatus == 0) {
            this.shareRecord(0000);
        } else {
            this.stopToRun();
        }
        this.mode();
    }//GEN-LAST:event_jButtonProductionActionPerformed
    /**
     * Üzemmódválasztás : Üzemszünet
     */
    private void jButtonOutageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOutageActionPerformed
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "jButtonOutageActionPerformed(evt) : Üzemszünet nyomógomb megnyomva");
        CentTerminal.mode = 2;
        this.shareRecord(9001);
        this.mode();
    }//GEN-LAST:event_jButtonOutageActionPerformed
    /**
     * Üzemmódválasztás TMK
     */
    private void jButtonMaintanenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMaintanenceActionPerformed
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "jButtonMaintanenceActionPerformed(evt) : TMK nyomógomb megnyomva");
        CentTerminal.mode = 3;
        this.shareRecord(9002);
        this.mode();
    }//GEN-LAST:event_jButtonMaintanenceActionPerformed

    @SuppressWarnings("SuspiciousIndentAfterControlStatement")
    private void jButtonAboutBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAboutBoxActionPerformed
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "jButtonAboutBoxActionPerformed(evt) : Névjegy nyomógomb megnyomva");
        if (this.centTerminalAboutBox == null) {
            this.centTerminalAboutBox = new CentTerminalAboutBox();
        }
        if (!this.centTerminalAboutBox.isVisible()) {
            this.centTerminalAboutBox.setLocationRelativeTo(this);
            this.centTerminalAboutBox.setVisible(true);
            CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "jButtonAboutBoxActionPerformed(evt) : Névjegy megjelenítve..");
        } else {
            CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "jButtonAboutBoxActionPerformed(evt) : Névjegy már meg lett jelenítve..");
        }
    }//GEN-LAST:event_jButtonAboutBoxActionPerformed
    /**
     * Node cim beállítása
     *
     * @param aNodeAddress
     */
    public void setNodeAddress(String aNodeAddress) {
        this.NodeAddress = aNodeAddress;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CentterminalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CentterminalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CentterminalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CentterminalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @SuppressWarnings("override")
            public void run() {
                new CentterminalFrame().setVisible(true);
            }
        });
    }
    /**
     * Álláskód kiválasztó fa módosítás eseménye.
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel codeText;
    private javax.swing.JLabel commLabel;
    private javax.swing.JLabel dbLabel;
    public javax.swing.JPanel dbPanel;
    private javax.swing.JLabel downTimeBegin;
    private javax.swing.JLabel downTimeEnd;
    private javax.swing.JButton jButtonAboutBox;
    private javax.swing.JButton jButtonMaintanence;
    private javax.swing.JButton jButtonModifyDownTimeCode;
    private javax.swing.JButton jButtonOutage;
    private javax.swing.JButton jButtonProduction;
    private javax.swing.JButton jButtonSetDownTimeCode;
    private javax.swing.JButton jButtonShareDownTime;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel machineState;
    private javax.swing.JLabel plcLabel;
    public javax.swing.JPanel plcPanel;
    private javax.swing.JLabel serverLabel;
    public javax.swing.JPanel serverPanel;
    private javax.swing.JTree treeCodeSelector;
    // End of variables declaration//GEN-END:variables

    /**
     * Üzemmódok kiválasztása 1:Termelés 2:Üzemszünet 3:TMK .
     */
    private synchronized void mode() {
        if (CentTerminal.mode == 1) {
            CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "mode() : Termelés");
            jButtonProduction.setBackground(Color.GREEN);
            jButtonOutage.setBackground(BUTTONDEFAULTCOLOR);
            jButtonMaintanence.setBackground(BUTTONDEFAULTCOLOR);
            jButtonProduction.setEnabled(false);
            jButtonOutage.setEnabled(true);
            jButtonMaintanence.setEnabled(true);
            if (CentTerminal.pLCSignals.plantStatus == 1) {
                treeCodeSelector.setEnabled(false);
            } else {
                treeCodeSelector.setEnabled(true);
            }
        } else if (CentTerminal.mode == 2) {
            CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "mode() : Üzemszünet");
            jButtonProduction.setBackground(BUTTONDEFAULTCOLOR);
            jButtonOutage.setBackground(Color.GREEN);
            jButtonMaintanence.setBackground(BUTTONDEFAULTCOLOR);
            jButtonProduction.setEnabled(true);
            jButtonOutage.setEnabled(false);
            jButtonMaintanence.setEnabled(true);
            this.collapseAll(treeCodeSelector);
            treeCodeSelector.setEnabled(false);

        } else if (CentTerminal.mode == 3) {
            CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "mode() : TMK");
            jButtonProduction.setBackground(BUTTONDEFAULTCOLOR);
            jButtonOutage.setBackground(BUTTONDEFAULTCOLOR);
            jButtonMaintanence.setBackground(Color.GREEN);
            jButtonProduction.setEnabled(true);
            jButtonOutage.setEnabled(true);
            jButtonMaintanence.setEnabled(false);
            this.collapseAll(treeCodeSelector);
            treeCodeSelector.setEnabled(false);

        }
    }

    /**
     * Kommunikációs hiba
     */
    public void commErr() {
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "com,Err() : Hiba a PLC kommunikációban");
        if (actRecord.code != -10003) {
            this.shareRecord(-10003);
        }
        refreshLabels("Hiba a PLC kommunikációban ");
        this.collapseAll();
        this.disableButtons();
        treeCodeSelector.setEnabled(false);
        communicationOk = false;
    }
 /**
     * Kommunikáció rendben Ha a kommunikáció rendbejött, akkor töröljük a kommunikációs hibát
     */
    public void commOk() {
        if (!communicationOk) {
            communicationOk = true;
            this.shareRecord(0);
            treeCodeSelector.setEnabled(true);
        }

    }

    /**
     * A megjelenítések frissétese
     */
    @SuppressWarnings({"UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
    public void refresh() {

        switch (CentTerminal.mode) {
            case 1: /*termelés*/

                /**
                 * Ha az állásidőkódot nem adták meg a beállított időn belül
                 * akkor beepeljen.
                 */
                if (CentTerminal.pLCSignals.plantStatus == 0) {
                    long downTimeLong;
                    if (CentTerminal.calculatedTimeEnable) {
                        downTimeLong = CentTerminal.calculatedTime.getTime() - CentTerminal.actRecord.downtimeStart;
                    } else {
                        downTimeLong = System.currentTimeMillis() + CentTerminal.sql.getDifTime() - CentTerminal.actRecord.downtimeStart;
                    }

                    if (CentTerminal.beep
                            && (downTimeLong)
                            > CentTerminal.beepTimeOut && CentTerminal.actRecord.code == 0) {

                        Toolkit.getDefaultToolkit().beep();
                        beepWorking = CentTerminal.beepClass.beep(beepWorking);
                    }

                    refreshLabels("A sor áll");
                    if (CentTerminal.actRecord.code < 9000) {
                        treeCodeSelector.setEnabled(true);
                    }
                    CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "refresh() : A sor áll");

                } else {
                    machineState.setText("A sor termel");
                    machineState.setForeground(Color.black);
                    codeText.setToolTipText(null);
                    downTimeBegin.setToolTipText(null);
                    downTimeEnd.setToolTipText(null);
                    this.collapseAll();
                    this.disableButtons();
                    this.defaultLabels();
                    treeCodeSelector.setEnabled(false);
                    CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "refresh() : A sor termel");
                }
                machineState.setToolTipText(recordText());
                break;
            case 2: /*üzemszünet*/

                if (actRecord.code != 9001) {
                    modeChangeCount++;
                    CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "refresh() : mod =2, record!=9001, modeChangeCount=" + modeChangeCount);
                    if (modeChangeCount > 3) {
                        CentTerminal.mode = 2;
                        this.shareRecord(9001);
                        this.mode();
                    }
                } else {
                    modeChangeCount = 0;
                }
                refreshLabels("Üzemszünet");
                treeCodeSelector.setEnabled(false);
                break;

            case 3:  /*tmk*/

                if (actRecord.code != 9002) {
                    modeChangeCount++;
                    CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "refresh() : mod =3, record!=9002, modeChangeCount=" + modeChangeCount);
                    if (modeChangeCount > 3) {
                        CentTerminal.mode = 3;
                        this.shareRecord(9002);
                        this.mode();
                    }
                } else {
                    modeChangeCount = 0;
                }
                refreshLabels("TMK");
                treeCodeSelector.setEnabled(false);
                break;
        }

    }

    private void refreshLabels(String mState) {

        machineState.setText(mState);
        machineState.setForeground(Color.red);
        downTimeBegin.setText(actRecord.downtimeStartString);
        downTimeBegin.setToolTipText("Leállás kezdete");
        long actTime;
        if (CentTerminal.calculatedTimeEnable) {
            actTime = CentTerminal.calculatedTime.getTime();
        } else {
            actTime = System.currentTimeMillis() + CentTerminal.sql.getDifTime();
        }
        downTimeEnd.setText(ActualDate.actualDate(actTime));
        downTimeEnd.setToolTipText("Leállás vége");
        codeText.setText(Integer.toString(actRecord.code));
        codeText.setToolTipText(toolTipText(actRecord.code));
    }

    /**
     * A recordhoz tartozó szövegek kiértékelése
     */
    private String recordText() {
        String recordText = "<html>";
        if (CentTerminal.records != null) {
            if (!CentTerminal.records.isEmpty()) {
                for (Record rec : CentTerminal.records) {
                    recordText += rec.downtimeStartString + " - " + rec.downtimeStopString + " - " + Integer.toString(rec.code) + " - " + toolTipText(rec.code) + "<br>";
                }
                recordText += "</html>";
            } else {
                recordText = null;
            }
        } else {
            recordText = null;
        }
        return recordText;
    }

    /**
     * Az aktuális kódhoz tartozó szöveg megjelenítése tooltiptextben
     */
    private String toolTipText(int code) {
        String toolTipText;
        if (code > 0 && code < 9000) {

            if (code % 1000 == 0) {
                toolTipText = codeTextCacheMap.get(code);
            } else {
                toolTipText = codeTextCacheMap.get(code - (code % 1000));
                if (code % 100 == 0) {
                    toolTipText += " - " + codeTextCacheMap.get(code);
                } else {
                    toolTipText += " - " + codeTextCacheMap.get(code - (code % 100));
                    if (code % 10 == 0) {
                        toolTipText += " - " + codeTextCacheMap.get(code);
                    } else {
                        toolTipText += " - " + codeTextCacheMap.get(code - (code % 10));
                        toolTipText += " - " + codeTextCacheMap.get(code);
                    }
                }
            }

        } else if (code == 9001) {
            toolTipText = "Üzemszünet";
        } else if (code == 9002) {
            toolTipText = "TMK";
        } else if (code == -10003) {
            toolTipText = "Kommunikációs hiba a PLC-vel";
        } else {
            toolTipText = "Még nem lett az álláskód rögzítve";
        }
        return toolTipText;
    }

    /**
     * Állásidő megosztása
     */
    private synchronized void shareRecord(int code) {
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "shareRecord(int " + code + ")");
        if (CentTerminal.actRecord.downtimeStart > 0) {
            CentTerminal.actRecord.downtimeStop = CentTerminal.sql.serverUnixTime();
            CentTerminal.actRecord.downtimeStopString = ActualDate.actualDate(CentTerminal.actRecord.downtimeStop);
            pushRecord();
            debugRecord();
        }

        CentTerminal.actRecord = new Record();
        CentTerminal.actRecord.downtimeStart = CentTerminal.sql.serverUnixTime();
        CentTerminal.actRecord.downtimeStartString = ActualDate.actualDate(CentTerminal.actRecord.downtimeStart);
        CentTerminal.actRecord.code = code;

    }

    /**
     * Állás recordok debugolása
     */
    private void debugRecord() {
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "Record: "
                + CentTerminal.actRecord.downtimeStartString + " "
                + CentTerminal.actRecord.downtimeStopString + " "
                + Integer.toString(CentTerminal.actRecord.code));
    }

    /**
     * Műszakváltás
     *
     * @param shiftChange
     */
    public synchronized void shiftChange(Long shiftChange) {
        if (CentTerminal.actRecord.downtimeStart > 0) {
            CentTerminal.actRecord.downtimeStop = shiftChange;
            CentTerminal.actRecord.downtimeStopString = ActualDate.actualDate(shiftChange);
            CentTerminal.records.add(CentTerminal.actRecord);
            debugRecord();

            code = CentTerminal.actRecord.code;
            CentTerminal.actRecord = new Record();
            CentTerminal.actRecord.downtimeStart = shiftChange;
            CentTerminal.actRecord.downtimeStartString = ActualDate.actualDate(shiftChange);
            CentTerminal.actRecord.code = code;
        }
    }

    /**
     * Nyomógombok tiltása
     */
    private void disableButtons() {
        jButtonModifyDownTimeCode.setEnabled(false);
        jButtonSetDownTimeCode.setEnabled(false);
        jButtonShareDownTime.setEnabled(false);
        // CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "disableButtons() : A nyomógombok letíltása");
    }

    /**
     * Szövegmezők engedélyezése
     */
    private void enableLabels() {
        downTimeEnd.setEnabled(true);
        downTimeBegin.setEnabled(true);
        codeText.setEnabled(true);
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "enableLabels() : A labelek engedélyezése");
    }

    /**
     * A mezők alapértelmezett értékbe állítása
     */
    private void defaultLabels() {
        machineState.setText("A sor termel");

        downTimeEnd.setText("Leállás vége");
        downTimeEnd.setEnabled(false);

        downTimeBegin.setText("Leállás kezdete");
        downTimeBegin.setEnabled(false);

        codeText.setText("Kód");
        codeText.setEnabled(false);
        // CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "defaultLabels() : A labelek alapértékbeállítása");
    }

    /**
     * A berendezés leáll
     */
    public synchronized void runToStop() {
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "runToStop() : A berendezés megállt");
        CentTerminal.actRecord = new Record();
        CentTerminal.actRecord.downtimeStart = CentTerminal.sql.serverUnixTime();
        CentTerminal.actRecord.downtimeStartString = ActualDate.actualDate(CentTerminal.actRecord.downtimeStart);
        CentTerminal.actRecord.code = 0;
        treeCodeSelector.setEnabled(true);
    }

    /**
     * A berendezés elindul
     */
    public synchronized void stopToRun() {
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "stopToRun() : A berendezés elindult");
        CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "stopToRun()");
        CentTerminal.actRecord.downtimeStop = CentTerminal.sql.serverUnixTime();
        CentTerminal.actRecord.downtimeStopString = ActualDate.actualDate(CentTerminal.actRecord.downtimeStop);
        pushRecord();
        debugRecord();
        CentTerminal.actRecord = new Record();
    }

    private synchronized void pushRecord() {
        if (actRecord.downtimeStop != -1 && actRecord.downtimeStart != 0) {
            CentTerminal.records.push(CentTerminal.actRecord);
        } else {
            CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "Hibás rekord :::::::::::::");
            debugRecord();
        }
    }

    /**
     * Állásidő kód választó bezárása
     */
    public void collapseAll() {
        this.collapseAll(treeCodeSelector);
    }

    /**
     * Állásidő kód választó bezárása
     */
    private void collapseAll(JTree tree) {
        int row = tree.getRowCount() - 1;
        while (row >= 0) {
            tree.collapseRow(row);
            row--;
        }
    }

}
