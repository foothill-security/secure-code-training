
package org.owasp.webgoat.plugin;

import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.Center;
import org.apache.ecs.html.H1;
import org.apache.ecs.html.HR;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TH;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.owasp.webgoat.lessons.Category;
import org.owasp.webgoat.lessons.LessonAdapter;
import org.owasp.webgoat.session.ECSFactory;
import org.owasp.webgoat.session.ParameterNotFoundException;
import org.owasp.webgoat.session.WebSession;
import org.owasp.webgoat.util.HtmlEncoder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


/***************************************************************************************************
 * 
 * 
 * This file is part of WebGoat, an Open Web Application Security Project utility. For details,
 * please see http://www.owasp.org/
 * 
 * Copyright (c) 2002 - 20014 Bruce Mayhew
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 * 
 * Getting Source ==============
 * 
 * Source for this application is maintained at https://github.com/WebGoat/WebGoat, a repository for free software
 * projects.
 * 
 * For details, please see http://webgoat.github.io
 * 
 * @author Ryan Knell <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @created July, 23 2007
 * Modified for Code review exercise by Jason White 
 * @modified 2019
 */

public class ConcurrencyCart extends LessonAdapter {
    // Shared Variables
    private static int total = 0;
    private static float runningTOTAL = 0;
    private static int subTOTAL = 0;
    private static float calcTOTAL = 0;
    private static int quantity1 = 0;
    private static int quantity2 = 0;
    private static int quantity3 = 0;
    private static int quantity4 = 0;
    private float ratio = 0;
    private int discount = 0;

    /**
     * Description of the Method
     * 
     * @param s
     *            Description of the Parameter
     * @return Description of the Return Value
     */

    protected String calculateRunningTotal(WebSession s) {
        try
        {
            String submit = s.getParser().getStringParameter("SUBMIT");

            if ("Purchase".equalsIgnoreCase(submit))
            {
                //User submits for purchase
                updateQuantity(s);
                ec = createPurchaseContent(s, quantity1, quantity2, quantity3, quantity4);
            } else if ("Confirm".equalsIgnoreCase(submit)) {
                //User confirms purchase
                //ec = 
                confirmation(s, quantity1, quantity2, quantity3, quantity4);

                // Discount

                if (calcTOTAL == 0) // No total cost for items
                {
                    discount = 0; // Discount meaningless
                }
                else
                // The expected case -- items cost something
                {
                    ratio = runningTOTAL / calcTOTAL;
                    return formatFloat(runningTOTAL)
                }

            } else {
                return 0;
            }

        } catch (ParameterNotFoundException pnfe) {
            // System.out.println("[DEBUG] no action selected, defaulting to createShoppingPage");
            return 0;
        }
        
    }

    // UPDATE QUANTITY VARIABLES
    private void updateQuantity(WebSession s) {
        quantity1 = thinkPositive(s.getParser().getIntParameter("QTY1", 0));
        quantity2 = thinkPositive(s.getParser().getIntParameter("QTY2", 0));
        quantity3 = thinkPositive(s.getParser().getIntParameter("QTY3", 0));
        quantity4 = thinkPositive(s.getParser().getIntParameter("QTY4", 0));
    }

    /*
     * PURCHASING PAGE
     */

    private void calculatePurchaseTotal(WebSession s, int quantity1, int quantity2, int quantity3,
            int quantity4) {

        runningTOTAL = 0;

        try {
            // for this exercise, imagine the values were pulled from a DB, not hardcoded ... humor me
            //Item 1
            total = quantity1 * 169;
            runningTOTAL += total;

            // Item 2
            total = quantity2 * 299;
            runningTOTAL += total;
            
            // Item 3
            total = quantity3 * 1799;
            runningTOTAL += total;

            // Item 4
            total = quantity4 * 649;
            runningTOTAL += total;
            ec.addElement(new BR());

            calcTOTAL = runningTOTAL;

        } catch (Exception e) {
            s.setMessage("Error generating " + this.getClass().getName());
            e.printStackTrace();
        }

    }

    /*
     * CONFIRMATION PAGE - Final purchcase and confirmation are made from here
     */

    private void confirmation(WebSession s, int quantity1, int quantity2, int quantity3, int quantity4) {

        //final String confNumber = "CONC-88";
        //TODO generate random confirmation number
        calcTOTAL = 0;
        try {
            
            // Item 1
            total = quantity1 * 169;
            calcTOTAL += total;

            // Item 2
            total = quantity2 * 299;
            calcTOTAL += total;

            // Item 3
            total = quantity3 * 1799;
            calcTOTAL += total;

            // Item 4
            total = quantity4 * 649;
            calcTOTAL += total;
        } catch (Exception e) {
            s.setMessage("Error generating " + this.getClass().getName());
            e.printStackTrace();
        }
    }

    String formatInt(int i) {
        NumberFormat intFormat = NumberFormat.getIntegerInstance(Locale.US);
        return intFormat.format(i);
    }

    String formatFloat(float f) {
        NumberFormat floatFormat = NumberFormat.getNumberInstance(Locale.US);
        floatFormat.setMinimumFractionDigits(2);
        floatFormat.setMaximumFractionDigits(2);
        return floatFormat.format(f);
    }

    int thinkPositive(int i) {
        if (i < 0)
            return 0;
        else
            return i;
    }

    /**
     * This can be ignored for purpose of this code review exercise
     * 
     * @return DOCUMENT ME!
     */
    protected Category getDefaultCategory() {
        return Category.CONCURRENCY;
    }

    /**
     * Gets the hints attribute of the AccessControlScreen object
     * These are original hints from the old WebGoat lesson
     * ...leaving in for code review exercise as it may be helpful
     * @return The hints value
     */
    protected List<String> getHints(WebSession s) {
        List<String> hints = new ArrayList<String>();
        hints.add("Can you purchase the merchandise in your shopping cart for a lower price?");
        hints.add("Try using a new browser window to get a lower price.");
        hints.add("In window A, purchase a low cost item. In window B, update the card with a high cost item.");
        hints.add("In window A, commit after updating cart in window B.");

        return hints;
    }

    /**
     * Gets the instructions attribute of the WeakAccessControl object
     * 
     * @return The instructions value
     */
    public String getInstructions(WebSession s)
    {
        String instructions = "For this exercise, your mission is to exploit the concurrency issue which will allow you to purchase merchandise for a lower price.";
        return (instructions);
    }

    private final static Integer DEFAULT_RANKING = new Integer(120);

    protected Integer getDefaultRanking()
    {
        return DEFAULT_RANKING;
    }

    /**
     * Gets the title attribute of the AccessControlScreen object
     * 
     * @return The title value
     */
    public String getTitle()
    {
        return "Shopping Cart Concurrency Flaw";
    }

}
