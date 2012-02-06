package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.dao.ExperimentDao;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import ch.ethz.origo.jerpa.data.tier.pojo.Person;
import ch.ethz.origo.jerpa.data.tier.pojo.Scenario;
import ch.ethz.origo.jerpa.data.tier.pojo.Weather;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static javax.swing.BorderFactory.*;

/**
 * @author Petr Miko
 *         <p/>
 *         Viewer of experiment information.
 */
public class ExperimentOverview extends JDialog implements ActionListener {

    private Experiment experiment;
    private Scenario scenario;

//    private final static Logger log = Logger.getLogger(ExperimentOverview.class);

    private final int LETTERS_VISIBLE = 20;
    private final int LINES_VISIBLE = 8;

    /**
     * Constructor. Creates the view in accordance to provided experiment id.
     *
     * @param experimentId experiment identifier
     */
    public ExperimentOverview(int experimentId) {
        super();
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ExperimentDao experimentDao = DaoFactory.getExperimentDao();
        experiment = experimentDao.get(experimentId);
        scenario = experiment.getScenario();
        HibernateUtil.rebind(scenario);

        this.setTitle(experiment.getExperimentId() + " - " + scenario.getTitle());

        JPanel canvas = new JPanel();
        initViewer(canvas);

        this.add(canvas);
        this.pack();
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Fills the canvas with panels to be visualized.
     *
     * @param canvas panel for display purposes
     */
    private void initViewer(JPanel canvas) {
        canvas.setLayout(new GridBagLayout());

        GridBagConstraints generalPaneConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createGeneralPane(), generalPaneConstraints);

        GridBagConstraints peoplePaneConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createPeoplePane(), peoplePaneConstraints);

        GridBagConstraints metaPaneConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createMetaPane(), metaPaneConstraints);

        GridBagConstraints timePaneConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createTimePane(), timePaneConstraints);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        closeButton.setActionCommand("close");

        GridBagConstraints closeButtonConstraints = new GridBagConstraints(0, 3, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0);
        canvas.add(closeButton, closeButtonConstraints);
    }

    /**
     * Creates panel with experiment meta information.
     *
     * @return panel with meta information of experiments'
     */
    private JPanel createMetaPane() {
        Border metaBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Meta information", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel metaPane = new JPanel(new GridBagLayout());
        metaPane.setBorder(metaBorder);

        Weather weather = experiment.getWeather();
        HibernateUtil.rebind(weather);

        JLabel tempLabel = new JLabel("Temperature");
        JLabel weatherLabel = new JLabel("Weather");
        JLabel weatherNoteLabel = new JLabel("Weather note");

        JTextField tempField = new JTextField(experiment.getTemperature() + " °C", LETTERS_VISIBLE);
        JTextField weatherField = new JTextField(weather.getTitle(), LETTERS_VISIBLE);
        JTextArea weatherNoteArea = new JTextArea(experiment.getWeathernote(), LINES_VISIBLE, LETTERS_VISIBLE);

        weatherField.setToolTipText(weather.getDescription());
        weatherNoteArea.setLineWrap(true);
        weatherNoteArea.setWrapStyleWord(true);
        tempField.setEditable(false);
        weatherField.setEditable(false);
        weatherNoteArea.setEditable(false);

        JScrollPane weatherNoteScroll = new JScrollPane(weatherNoteArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints tempLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints tempFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints weatherLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints weatherFieldConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints weatherNoteLabelConstraints = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints weatherNoteAreaConstraints = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        metaPane.add(tempLabel, tempLabelConstraints);
        metaPane.add(tempField, tempFieldConstraints);
        metaPane.add(weatherLabel, weatherLabelConstraints);
        metaPane.add(weatherField, weatherFieldConstraints);
        metaPane.add(weatherNoteLabel, weatherNoteLabelConstraints);
        metaPane.add(weatherNoteScroll, weatherNoteAreaConstraints);

        return metaPane;
    }

    /**
     * Creates panel with general information about experiment.
     *
     * @return panel with general information
     */
    private JPanel createGeneralPane() {
        Border generalBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "General information", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel generalPane = new JPanel(new GridBagLayout());
        generalPane.setBorder(generalBorder);

        JLabel idLabel = new JLabel("Experiment id");
        JLabel nameLabel = new JLabel("Scenario name");
        JTextField idField = new JTextField("" + experiment.getExperimentId(), LETTERS_VISIBLE);
        JTextField nameField = new JTextField(scenario.getTitle(), LETTERS_VISIBLE);
        GridBagConstraints idLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints idFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints nameLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints nameFieldConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        idField.setEditable(false);
        nameField.setEditable(false);
        generalPane.add(idLabel, idLabelConstraints);
        generalPane.add(idField, idFieldConstraints);
        generalPane.add(nameLabel, nameLabelConstraints);
        generalPane.add(nameField, nameFieldConstraints);

        return generalPane;
    }

    /**
     * Creates panel with experiment's time information.
     *
     * @return panel with times
     */
    private JPanel createTimePane() {
        Border timeBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Time", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel timePane = new JPanel(new GridBagLayout());
        timePane.setBorder(timeBorder);

        Date startTime = experiment.getStartTime();
        Date endTime = experiment.getEndTime();

        DateFormat timeFormat = new SimpleDateFormat("d.M.yyyy HH:mm:ss");

        JLabel startLabel = new JLabel("Start time");
        JLabel endLabel = new JLabel("End time");
        JTextField startField = new JTextField(timeFormat.format(startTime), LETTERS_VISIBLE);
        JTextField endField = new JTextField(timeFormat.format(endTime), LETTERS_VISIBLE);

        startField.setEditable(false);
        endField.setEditable(false);

        GridBagConstraints startLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints startFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints endLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints endFieldConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        timePane.add(startLabel, startLabelConstraints);
        timePane.add(startField, startFieldConstraints);
        timePane.add(endLabel, endLabelConstraints);
        timePane.add(endField, endFieldConstraints);

        return timePane;
    }

    /**
     * Creates panel with information about people involved in experiment.
     *
     * @return panel with people names
     */
    private JPanel createPeoplePane() {
        Border peopleBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "People", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel peoplePane = new JPanel(new GridBagLayout());
        peoplePane.setBorder(peopleBorder);

        Person owner = experiment.getOwner();
        Person subject = experiment.getSubject();
        HibernateUtil.rebind(owner);
        HibernateUtil.rebind(subject);

        JLabel ownerLabel = new JLabel("Owner");
        JLabel subjectLabel = new JLabel("Subject");
        JTextField ownerField = new JTextField(owner.getName() + " " + owner.getSurname(), LETTERS_VISIBLE);
        JTextField subjectField = new JTextField(subject.getName() + " " + subject.getSurname(), LETTERS_VISIBLE);

        ownerField.setEditable(false);
        subjectField.setEditable(false);

        GridBagConstraints ownerLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints ownerFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints subjectLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints subjectFieldConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        peoplePane.add(ownerLabel, ownerLabelConstraints);
        peoplePane.add(ownerField, ownerFieldConstraints);
        peoplePane.add(subjectLabel, subjectLabelConstraints);
        peoplePane.add(subjectField, subjectFieldConstraints);
        return peoplePane;
    }

    public void actionPerformed(ActionEvent action) {
        if ("close".equals(action.getActionCommand())) {
            this.dispose();
        }
    }
}