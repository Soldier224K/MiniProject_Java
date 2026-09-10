package com.hospital.ui;

import com.hospital.model.Patient;
import com.hospital.service.HospitalService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Sorted Records & Data Structures Inspector Panel.
 * Demonstrates TreeMap sorting features (O(log N) operations) and displays
 * an educational comparison of Arrays, LinkedList, HashMap, and TreeMap.
 */
public class SortedRecordsPanel extends JPanel {
    private final HospitalService service = HospitalService.getInstance();
    private final MainFrame mainFrame;

    private DefaultTableModel tableModel;
    private JTable sortedTable;
    private JComboBox<String> cmbSortCriteria;
    private JLabel lblSortExplanation;

    public SortedRecordsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BG_MAIN);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initComponents();
        applySorting();
    }

    private void initComponents() {
        // Top Header
        add(UIConstants.createHeaderBanner(
            "Sorted Patient Records & Data Structures Architecture (TreeMap)",
            "Explore logarithmic O(log N) ordered trees, custom sorting, and hospital data structure mapping"
        ), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setOpaque(false);

        // --- TOP CONTROLS & EXPLANATION BANNER ---
        JPanel topCard = UIConstants.createCardPanel();
        topCard.setLayout(new BorderLayout(12, 12));

        JPanel controlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        controlRow.setOpaque(false);

        JLabel lblSelect = new JLabel("Select Sort Criterion (TreeMap & Comparator):");
        lblSelect.setFont(UIConstants.FONT_BODY_BOLD);

        cmbSortCriteria = new JComboBox<>(new String[]{
            "Alphabetical by Patient Name (TreeMap Case-Insensitive)",
            "Natural Order by Patient ID (TreeMap Red-Black Tree)",
            "Youngest to Oldest (Age Ascending)",
            "Oldest to Youngest (Age Descending)",
            "Most Recent Registrations First (Date Descending)"
        });
        cmbSortCriteria.setFont(UIConstants.FONT_BODY);
        cmbSortCriteria.addActionListener(e -> applySorting());

        JButton btnRefresh = UIConstants.createPrimaryButton("Re-apply Sort");
        btnRefresh.addActionListener(e -> applySorting());

        controlRow.add(lblSelect);
        controlRow.add(cmbSortCriteria);
        controlRow.add(btnRefresh);

        lblSortExplanation = new JLabel("Active Sorting: TreeMap<String, Patient> maintains entries sorted automatically.");
        lblSortExplanation.setFont(UIConstants.FONT_BODY);
        lblSortExplanation.setForeground(UIConstants.PRIMARY_DARK);

        topCard.add(controlRow, BorderLayout.NORTH);
        topCard.add(lblSortExplanation, BorderLayout.SOUTH);

        content.add(topCard, BorderLayout.NORTH);

        // --- CENTER: SORTED TABLE ---
        JPanel tableCard = UIConstants.createCardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        String[] cols = {"Index", "Patient ID", "Full Name", "Age", "Gender", "Blood Group", "Contact Number", "Reg. Date", "BP"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        sortedTable = new JTable(tableModel);
        UIConstants.styleTable(sortedTable);

        JScrollPane scrollPane = new JScrollPane(sortedTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        content.add(tableCard, BorderLayout.CENTER);

        // --- BOTTOM: DATA STRUCTURES COMPARISON CARD ---
        JPanel dsCard = UIConstants.createCardPanel();
        dsCard.setLayout(new BorderLayout(8, 8));

        JLabel lblDsTitle = new JLabel("Data Structure Selection & Academic Mapping Summary");
        lblDsTitle.setFont(UIConstants.FONT_HEADER);
        lblDsTitle.setForeground(UIConstants.TEXT_MAIN);

        String[] dsCols = {"Data Structure", "Hospital Usage in System", "Time Complexity", "Key Justification"};
        String[][] dsData = {
            {"Arrays (T[])", "Blood Groups, Departments, Room Types, Recent ID Buffer", "O(1) index, O(N) search", "Fixed categories & fast direct indexing memory layout"},
            {"LinkedList<T>", "Appointments Queue, Patient Clinical Records History", "O(1) insert/delete, O(N) traversal", "Sequential consultations, dynamic FIFO queue ordering"},
            {"HashMap<K, V>", "Patient ID Lookup, Doctor & Billing Invoicing", "O(1) average lookup/insert", "Instant instantaneous patient search by Unique ID"},
            {"TreeMap<K, V>", "Alphabetical Patient Directory & ID Indexing", "O(log N) guaranteed sort", "Red-Black Tree ensures balanced in-order traversal"}
        };

        DefaultTableModel dsModel = new DefaultTableModel(dsData, dsCols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable dsTable = new JTable(dsModel);
        UIConstants.styleTable(dsTable);
        dsTable.setPreferredScrollableViewportSize(new Dimension(800, 100));

        JScrollPane dsScroll = new JScrollPane(dsTable);
        dsScroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));

        dsCard.add(lblDsTitle, BorderLayout.NORTH);
        dsCard.add(dsScroll, BorderLayout.CENTER);

        content.add(dsCard, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);
    }

    public void applySorting() {
        int index = cmbSortCriteria.getSelectedIndex();
        List<Patient> list;

        switch (index) {
            case 0:
                // Sorted by Name via TreeMap
                list = service.getPatientsSortedByName();
                lblSortExplanation.setText("✓ Using TreeMap<String, Patient> (Case-Insensitive Red-Black Tree) sorted by Patient Name.");
                break;
            case 1:
                // Sorted by ID via TreeMap
                list = service.getPatientsSortedById();
                lblSortExplanation.setText("✓ Using TreeMap<String, Patient> sorted naturally by Unique Patient ID (PAT-xxxx).");
                break;
            case 2:
                list = service.getPatientsSortedByAge(true);
                lblSortExplanation.setText("✓ Sorted dynamically by Age (Ascending: Youngest to Oldest).");
                break;
            case 3:
                list = service.getPatientsSortedByAge(false);
                lblSortExplanation.setText("✓ Sorted dynamically by Age (Descending: Senior citizens first).");
                break;
            case 4:
                list = service.getPatientsSortedByRegistrationDate(true);
                lblSortExplanation.setText("✓ Sorted by Registration Date (Most recent admissions first).");
                break;
            default:
                list = service.getPatientsSortedByName();
                break;
        }

        tableModel.setRowCount(0);
        int rank = 1;
        for (Patient p : list) {
            tableModel.addRow(new Object[]{
                rank++,
                p.getId(),
                p.getName(),
                p.getAge() + " yrs",
                p.getGender(),
                p.getBloodGroup(),
                p.getContactNumber(),
                p.getRegistrationDate(),
                p.getBloodPressure()
            });
        }
    }
}
