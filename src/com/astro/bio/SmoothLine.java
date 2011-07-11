/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.astro.bio;


import java.awt.geom.*; 
import  org.jfree.data.time.*; 
import java.util.*; 
import org.jfree.data.XYSeries;


public class SmoothLine { 
   /** Total number of samples that the class will return with the dataset. */ 
   private int samples = 100; 
   /** The smooth factor determinates how open are the bezier curves, and also how much do they fit to the data. */ 
   private double smooth_factor = 0.75; 
   /** The points given by the user to be smoothed*/ 
   private Point2D[] points; 

   /** 
    * Constructor from a Point2D array. 
    * @param points The Point2D array that will be used to get the reference points for the smooth line. 
    */ 
   public SmoothLine(Point2D[] points){ 
      this.points = points; 
   } 
    
   public SmoothLine(TimeSeries time_serie){ 
       
      //First, we set the Points2D array used to store the data 
      points = new Point2D[time_serie.getItemCount()]; 
      //Fill the array 
      for(int i = 0; i < time_serie.getItemCount(); i++){ 
      points[i] = new Point2D.Double(new Double(time_serie.getTimePeriod(i).getFirstMillisecond()), time_serie.getValue(i).doubleValue()); 
      } 
   } 
    
   /** 
    * Gets and returns a Point2D array with int samples that can be used to 
    * fill a serie, or whatever. 
    * @param points the array of points to interpolate the Bezier lines. 
    * @param samples number of sampels to return. 
    * 
    * */ 
public Point2D[] getPoint2DArray() { 
    
    double x1=0; 
     double y1=0; 
     double x2=0; 
     double y2=0; 
      
     double x0; 
     double y0; 
     double x3; 
     double y3; 
      
     int samples_interval = Math.round(samples / (points.length-1)); 
    Point2D[] points_return = new Point2D[samples]; 
    int pos_return = 0; //we'll store the pointer in the points_return array here. 
    //We iterate between the different given points, 
     //calculating the Bezier curves between them 
   for(int i=0; i < points.length-1; i++){ 
      //the last period may have a different number of samples in order to fit the sample value 
      if(i == points.length-2){ 
         samples_interval = samples - (samples_interval*(points.length-2)); 
      } 
      x1=points[i].getX(); 
         x2=points[i+1].getX(); 
         y1=points[i].getY(); 
         y2=points[i+1].getY(); 
         if(i>0){ 
         x0=points[i-1].getX(); 
         y0=points[i-1].getY(); 
         }else { 
         x0 = x1 - Math.abs(x2 - x1); 
         y0 = y1; 
         } 
         if(i < points.length -2){ 
         x3=points[i+2].getX(); 
         y3=points[i+2].getY(); 
         } else { 
         x3 = x1 + 2*Math.abs(x1 - x0); 
         y3 = y1; 
         } 
         Point2D[] points_bezier =  CalculateBezierCurve(x0,y0,x1,y1,x2,y2,x3,y3, samples_interval); 
         //Fill the return array 
         for(int j = 0 ; j < points_bezier.length; j++){ 
            points_return[pos_return] = new Point2D.Double(points_bezier[j].getX(),points_bezier[j].getY()); 
            pos_return++; 
         } 
          
   } 
    

   return points_return; 
} 
/** 
 * Takes the points, calculates the Bezier curves and creates a XYSeries. 
 * @param serie_name The name of the serie that will appear in the Legend, for example. 
 * @return the XYSerie ready to be used. 
 * */ 
public XYSeries getXYSeries(String serie_name){ 
   XYSeries serie = new XYSeries(serie_name); 
   Point2D[] points_bezier = getPoint2DArray(); 
   for(int j = 0 ; j < points_bezier.length; j++){ 
       
      serie.add(points_bezier[j].getX(),points_bezier[j].getY()); 
   } 
   return serie; 
} 
/** 
 * Takes the points stored and creates a TimeSeries with a smooth line. If the contructor is not the one with TimeSeries as the parameter, the x value has to be UnixTimestamp in milliseconds. 
 * @param serie_name 
 * @return a TimeSeries with the name given as parameter and a Second.class as the RegularTimePeriod, to be able to interpolate. 
 */ 
public TimeSeries getTimeSeries(String serie_name){ 
   //First, we create the TimeSeries to return 
   TimeSeries time_serie= new TimeSeries(serie_name,Second.class); 
    
   //Gets the Bezier curves 
   Point2D[] points_bezier = getPoint2DArray(); 
    
   for(int j = 0 ; j < points_bezier.length; j++){ 
      //System.out.println(j + " X --> " + points_bezier[j].getX()); 
      //time_serie.add(RegularTimePeriod.createInstance(Second.class,new Date((long)points_bezier[j].getX()),RegularTimePeriod.DEFAULT_TIME_ZONE),points_bezier[j].getY()); 
      time_serie.add(new Second(new Date((long)points_bezier[j].getX())),points_bezier[j].getY()); 
   } 
    
    
   return time_serie; 
} 
/** 
 * Calculates the Bezier curve between two points 
 * The idea is taken from the PEAR Image_Graph Smoothline 
 * 
 * 
 * @param x0 the point "just before" (<code>null</code> permitted). 
 * @param y0 the point "just before" (<code>null</code> permitted). 
 * @param x1 the actual point to calculate the control points for (<code>null</code> NOT permitted). 
 * @param y1 the actual point to calculate the control points for (<code>null</code> NOT permitted). 
 * @param x2 the point "just after" (<code>null</code> NOT permitted). 
 * @param y2 the point "just after" (<code>null</code> NOT permitted). 
 * @param x3 the point "just after" x2 (<code>null</code> permitted). 
 * @param y4 the point "just after" y2 (<code>null</code> permitted). 
 * @param samples_interval number of points generated between the given points 
 * */ 
private Point2D[] CalculateBezierCurve(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, int samples_interval){ 
    
    
    
   double control_point_1_x; 
    double control_point_1_y; 
    double control_point_2_x; 
    double control_point_2_y; 
    
    
     //Calculate the control points for the cubic bezier line 
       control_point_1_x =controlPoint(x0,x1,x2); 
     control_point_1_y =controlPoint(y0,y1,y2); 
                
     control_point_2_x = controlPoint(x3,x2,x1); 
     control_point_2_y = controlPoint(y3,y2,y1); 
    
    //System.out.println("control1: " + control_point_1_x + " -- " + control_point_1_y); 
    //System.out.println("control2: " + control_point_2_x + " -- " + control_point_2_y); 
    
    
    double cx = 3.0 * (control_point_1_x - x1); 
    double bx = 3.0 * (control_point_2_x - control_point_1_x) - cx; 
    double ax = x2 - x1 - cx - bx; 
    
    double cy = 3.0 * (control_point_1_y - y1); 
    double by = 3.0 * (control_point_2_y - control_point_1_y) - cy; 
    double ay = y2 - y1 - cy - by; 
    
    //Let's calculate all the ponits that follow the Bezier curve. 
    Point2D[] points = new Point2D[samples_interval]; 
     for(int j = 0; j < samples_interval; j++){ 
       double t = j*(1.0/samples_interval); 
       //System.out.println("j: " + j + " t: " + t + " samples_int: " + (samples_interval)); 
       double x = (ax * t * t * t) + (bx * t * t) + (cx * t) + x1; 
       double y = (ay * t * t * t) + (by * t *t) + (cy * t) + y1; 
       //System.out.println("x: " + x + " y: " + y + " t: " + t); 
       points[j] = new Point2D.Double(x,y); 
     } 
     return points; 
   } 

    private double controlPoint(double p1, double p2, double factor){ 
               
     double sa = p2 + smooth_factor * (p2 -p1); 
     double sb = (p2+ sa)/2; 
     double m = (p2+ factor)/2; 
     return (sb + m)/2; 
    } 
    /** 
     * Returns the numebr of samples used to calculate the smooth line. 
     * @return the number of samples. 
     */ 
    public int getSamples(){ 
       return this.samples; 
    } 
    /** 
     * Sets the number of samples to be used when calculating the smooth line. 
     * @param samples the new number of samples (by default is 100) 
     */ 
    public void setSamples(int samples){ 
       this.samples = samples; 
    } 
    /** 
     * Returns the smooth factor that is set to be used for calculating the Bezier curves. 
     * @return the smooth factor. 
     */ 
    public double getSmooth_factor(){ 
       return this.smooth_factor; 
    } 
    /** 
     * Sets the smooth factor that will be used to calculate the Bezier curves. 
     * @param samples the new smooth factor (by default is 0.75) 
     */ 
    public void setSmooth_factor(double smooth_factor){ 
       this.smooth_factor = smooth_factor; 
    } 
    /** 
     * Returns either the points entered by the user (if the Point2D[] where passed through the contructor) or the calculated Point2D[] if another option was used. 
     * @return The Point2d array that will be used to calculate the Bezier curves 
     */ 
    public Point2D[] getGivenPoints(){ 
       return this.points; 
    } 
} 


