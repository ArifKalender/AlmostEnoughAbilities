package me.arifkalender.projectkorra.almostenoughabilities.util;

import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class UtilizationMethods {
    /**
     * Returns a list of all abilities around a point within a given radius.
     *
     * @param location The point where the method will look for abilities.
     * @param radius   The radius around the point
     */
    public static List<CoreAbility> getAbilitiesAroundPoint(Location location, double radius) {
        List<CoreAbility> toReturn = new ArrayList<>();
        for(CoreAbility ability : CoreAbility.getAbilitiesByInstances()){
            if(ability.getLocation() != null && ability.getLocation().distance(location) <= radius){
                toReturn.add(ability);
            }
        }
        return toReturn;
    }

    public static List<Location> getSpherePoints(Location origin, double radius, int pointCount) {
        List<Location> points = new ArrayList<>();
        World world = origin.getWorld();

        for (int i = 0; i < pointCount; i++) {
            double phi = Math.acos(1 - 2.0 * i / pointCount); // polar angle
            double theta = Math.PI * (1 + Math.sqrt(5)) * i;  // golden angle

            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.cos(phi);
            double z = radius * Math.sin(phi) * Math.sin(theta);

            points.add(origin.clone().add(x, y, z));
        }

        return points;
    }

    public static List<Location> getRingXZ(Location center, double radius, int pointCount) {
        List<Location> locations = new ArrayList<>();

        for (int i = 0; i < pointCount; i++) {
            double angle = 2 * Math.PI * i / pointCount;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            locations.add(center.clone().add(x, 0, z));
        }

        return locations;
    }
    public static List<Location> getRingXYZ(Location origin, Vector direction, double radius, int points) {
        List<Location> locations = new ArrayList<>();
        Location center = origin.clone().add(direction.clone().normalize().multiply(1.5));
        Vector normalizedDir = direction.clone().normalize();
        Vector up = new Vector(0, 1, 0);
        Vector right = normalizedDir.clone().crossProduct(up).normalize();
        Vector forward = right.clone().crossProduct(normalizedDir).normalize();

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;
            Vector offset = right.clone().multiply(x).add(forward.clone().multiply(y));
            locations.add(center.clone().add(offset));
        }

        return locations;
    }

}
