package cc.lasmgratel.lwcautosell;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Config implements ConfigurationSerializable {
    private Set<Location> locations;

    public Config() {
        locations = new HashSet<>();
    }

    public Config(Map<String, Object> map) {
        locations = (Set<Location>) map.getOrDefault("locations", new HashSet<>());
    }

    public Set<Location> getLocations() {
        return locations;
    }

    @Override
    public Map<String, Object> serialize() {
        return Collections.singletonMap("locations", locations);
    }
}
