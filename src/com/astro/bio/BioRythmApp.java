package com.astro.bio;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author praveenm
 */
public class BioRythmApp {

    final static class Listener implements ActionListener {

        /*
         * 
         * 
         * The equations for the curves are

physical: sin(2?t / 23), 
emotional: sin(2?t / 28), 
intellectual: sin(2?t / 33), 
intuitive: sin(2?t / 38), 
where t indicates the number of days since birth.
*/
        ObservingTextField textField;

        public Listener(ObservingTextField textField) {
            this.textField = textField;
        }
        boolean v;

        public Listener(ObservingTextField textField, boolean v) {
            this.textField = textField;
            this.v = v;
        }

        public void actionPerformed(ActionEvent e) {
            textField.setEditable(false);
            DatePicker dp = new DatePicker(textField, Locale.US);
            // previously selected date
            Date selectedDate = DatePicker.parseDate(textField.getText());
            dp.setSelectedDate(selectedDate);
            textField.d = selectedDate;
            //System.out.println("%%% $$$$" + textField.d + selectedDate);
            dp.start(textField);
        }
    }
    static final JFrame frame = new JFrame();

    public static void main(String[] args) {
        final BioRythmApp b = new BioRythmApp();

        JPanel jp = new JPanel() {

            {
                setLayout(new GridLayout(4, 1));
                final JButton btn = new JButton("Select the date of Birth");
                final ObservingTextField dobField = new ObservingTextField(10);
                final JComboBox box = new JComboBox();
                box.addItem(15);
                box.addItem(30);
                box.addItem(45);
                box.addItem(60);
                
                box.setSelectedIndex(1);
                //box.setEditable(true);

                add(new JLabel("Date Of Birth (MM/DD/YYYY)"));
                add(dobField);add(btn);
                btn.addActionListener(new Listener(dobField));

                final JButton btn1 = new JButton("Select Start Date");
                final ObservingTextField startDateField = new ObservingTextField(10);
                btn1.addActionListener(new Listener(startDateField));
                add(new JLabel("Starting From (MM/DD/YYYY)"));
                add(startDateField);add(btn1);

                add(new JLabel("Days"));
                add(box);
                JButton go = new JButton("Draw");
                go.addActionListener(new ActionListener() {

                    private boolean validate(JTextField... jf){
                        if(jf==null) return false;
                        for(JTextField j: jf){
                            String s= j.getText();
                            //System.out.println(" Val : "+s+" : ");
                        if(s==null || s.equals("")){
                            return false;
                        }
                        }
                        return true;
                    }
                    public void actionPerformed(ActionEvent e) {


                        if(!validate(startDateField,dobField)){
                            JOptionPane.showMessageDialog(null, "Please Enter Dates in correct format");
                            return;
                        }
                         
                        Date d1 = DatePicker.parseDate(startDateField.getText());
                        Date d2 = DatePicker.parseDate(dobField.getText());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                           d1= sdf.parse(startDateField.getText());
                           d2=sdf.parse(dobField.getText());
                        } catch (Exception ex) {
                            d1=null;
                            d2=null;
                        }
                        System.out.println(dobField.getText()+"DOB :"+sdf.format(d2));
                        System.out.println(startDateField.getText()+" Start :"+sdf.format(d1));

                        if(d1==null || d2==null){
                            JOptionPane.showMessageDialog(null, "Invalid Dates");
                            return;
                        }
                        if(!d1.after(d2)){
                            JOptionPane.showMessageDialog(null, "Date of Birth has to before the start date");
                            return;
                        }
                        long dur = DateUtils.getDaysInBetweenDates(d1,
                                d2);
                        Calendar start = Calendar.getInstance();
                        start.setTime(DatePicker.parseDate(dobField.getText()));
                        Calendar end = Calendar.getInstance();
                        Date now = new Date();
                        end.setTime(DatePicker.parseDate(startDateField.getText()));

                        dur = DateUtils.diffDayPeriods(start, end);
                        System.out.println("Duration : " + dur);
                        double d = Cycle.overAll(dur);
                        System.out.println("" + now.getYear() + " : " + now.getMonth() + " " + now.getDate());
                        days = 30;
                        try {
                            Integer a = (Integer) box.getSelectedItem();
                            days = a.intValue();
                        } catch (Throwable th) {
                            JOptionPane.showMessageDialog(frame, "Please enter a valid number");
                            return;
                            //th.printStackTrace();
                        }

                        JPanel jp = monthlyChart(end.get(Calendar.YEAR),
                                end.get(Calendar.MONTH), end.get(Calendar.DATE),
                                getData(dur));
                        b.updateUI(jp);
                        System.out.println("O :" + (double) d);
                    }
                });
                add(go);
            }
        };

        frame.add(jp, BorderLayout.NORTH);
        frame.setSize(900, 400);
        frame.setVisible(true);
        frame.setTitle("Bio Rythimc Cycles");
        frame.setIconImage(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
         
    }
    static{
        try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                //exc.printStackTrace();
            } catch (Exception e) {
                // Do nothing...
            }

    }
    JPanel jpn = null;
    static int days = 30;

    private final void updateUI(JPanel jp) {
        if (jpn != null) {
            frame.remove(jpn);
        }
        frame.add(jp);
        frame.validate();
        jpn = jp;


    }

    private static final Map<Cycle, List<Double>> getData(long dur) {
        Map<Cycle, List<Double>> map = new HashMap<Cycle, List<Double>>();
        List<Double> list = new ArrayList<Double>();
        for (int j = 0; j < days; j++) {
            list.add(Cycle.Intellectual.percent(dur + j));
        }
        map.put(Cycle.Intellectual, list);

        list = new ArrayList<Double>();
        for (int j = 0; j < days; j++) {
            list.add(Cycle.Physical.percent(dur + j));
        }
        map.put(Cycle.Physical, list);

        list = new ArrayList<Double>();
        for (int j = 0; j < days; j++) {
            list.add(Cycle.Emotional.percent(dur + j));
        }
        map.put(Cycle.Emotional, list);

        list = new ArrayList<Double>();
        for (int j = 0; j < days; j++) {
            list.add(Cycle.overAll(dur + j));
        }
        map.put(null, list);

        return map;

    }

    final static class ObservingTextField extends JTextField implements Observer {

        Date d = new Date();

        private ObservingTextField(int i) {
            super(i);

        }

        public void update(Observable o, Object arg) {
            Calendar calendar = (Calendar) arg;
            DatePicker dpicker = (DatePicker) o;
            setText(dpicker.formatDate(calendar,"dd/MM/yyyy"));
        }
    }

    /**
     * Creates and returns a line chart showing the number of hits
     * per day. The data is arbitrary.
     */
    private static final JPanel monthlyChart(int year, int month, int date, Map<Cycle, List<Double>> map) {
        TimeSeries intCycle = new TimeSeries("Intellectual", Day.class);
        TimeSeries physicalCycle = new TimeSeries("Physycal", Day.class);
        TimeSeries emotional = new TimeSeries("Emotional", Day.class);
        TimeSeries overall = new TimeSeries("Overall", Day.class);
        TimeSeriesCollection set = new TimeSeriesCollection();


        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        List<Double> iMap1 = map.get(Cycle.Intellectual);
        List<Double> pMap = map.get(Cycle.Physical);
        List<Double> eMap = map.get(Cycle.Emotional);
        List<Double> oMap = map.get(null);

        for (int i = 0; i < days; i++) {
            intCycle.add(new Day(calendar.getTime()), iMap1.get(i));// new Random().nextInt(200));

            physicalCycle.add(new Day(calendar.getTime()), pMap.get(i));// new Random().nextInt(200));

            emotional.add(new Day(calendar.getTime()), eMap.get(i));// new Random().nextInt(200));

            overall.add(new Day(calendar.getTime()), oMap.get(i));// new Random().nextInt(200));

            calendar.add(Calendar.DATE, 1);
        }

        SmoothLine s1 = new SmoothLine(physicalCycle);
        s1.setSamples(300); 
        s1.setSmooth_factor(1.0f); 
        set.addSeries(new SmoothLine(intCycle).getTimeSeries("Intellectual"));
        set.addSeries(s1.getTimeSeries("Physical"));
        set.addSeries(new SmoothLine(emotional).getTimeSeries("Emotional"));
        set.addSeries(new SmoothLine(overall).getTimeSeries("Overall"));
        set.setXPosition(TimePeriodAnchor.START);
        set.setDomainIsPointsInTime(true);

        JFreeChart bioChart =
                ChartFactory.createTimeSeriesChart("Bio Rythmic Cycle",
                String.valueOf(year), "More is Better",
                set,
                true,
                true,
                true);
        bioChart.setAntiAlias(true);
        bioChart.setBorderVisible(true);
        //bioChart.setBackgroundPaint(Color.white);

        ChartPanel cp = new ChartPanel(bioChart);
        return cp;
    }
}
