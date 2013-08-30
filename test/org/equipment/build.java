package org.equipment;

import org.rs2server.rs2.model.CombatNPCDefinition;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Equipment.EquipmentType;
import org.rs2server.rs2.model.equipment.EquipmentAnimations;
import org.rs2server.rs2.model.equipment.EquipmentDefinition;
import org.rs2server.util.XMLController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;

import static org.rs2server.rs2.model.ItemDefinition.*;

/**
 * Created with IntelliJ IDEA.
 * User: Vhincent
 * Date: 4/21/12
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class build {

    /**
     * The <code>EquipmentDefinition</code> map.
     */
    private static Map<Integer, EquipmentDefinition> definitions;

    public static void maidn(String[] args) {
        int[] nums = new int[args.length + 1];
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter 5 numbers on one line commas:");
        sc.useDelimiter(",");
        for (int i = 0; i < nums.length; i++) {
            nums[i] = sc.nextInt();
            System.out.print(nums[i]);
        }
    }

    public static void main(String args[]) throws IOException {
        InputStreamReader istream = new InputStreamReader(System.in);
        BufferedReader bufRead = new BufferedReader(istream);
        EquipmentType equipmentType = null;
        System.out.println("Item ID:");
        int id = Integer.parseInt(bufRead.readLine());

        System.out.println("Type:");
        equipmentType = EquipmentType.valueOf(bufRead.readLine().toUpperCase());

        System.out.println("Bonuses:");
        int[] nums = new int[13];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = Integer.parseInt(bufRead.readLine());
        }

        System.out.println("Has any Requirements?:");
        EquipmentDefinition.Skill skill = null;
        if (bufRead.readLine().equalsIgnoreCase("yes")) {
            System.out.println("Skill:");
            skill = EquipmentDefinition.Skill.valueOf(bufRead.readLine().toUpperCase());
            System.out.println(skill);
        } else if (bufRead.readLine().equalsIgnoreCase("no")) {

        } else {
            System.out.println("Please enter yes or no.");
            return;
        }


        /*  int[] bonus = new int[]{Integer.parseInt(bufRead.readLine()),
                Integer.parseInt(bufRead.readLine()),
                Integer.parseInt(bufRead.readLine()),
                Integer.parseInt(bufRead.readLine()),
                Integer.parseInt(bufRead.readLine()),
                Integer.parseInt(bufRead.readLine()),
                Integer.parseInt(bufRead.readLine()),
                Integer.parseInt(bufRead.readLine()),
                Integer.parseInt(bufRead.readLine()),
                Integer.parseInt(bufRead.readLine()), Integer.parseInt(bufRead.readLine()), Integer.parseInt(bufRead.readLine()), Integer.parseInt(bufRead.readLine()),};
        */
        System.out.println("Dumped ");


        XMLController.writeXML(definitions, new File("data/equipmentDefinition.xml"));

    }
}
