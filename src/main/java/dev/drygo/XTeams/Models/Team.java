package dev.drygo.XTeams.Models;

import dev.drygo.XTeams.Utils.PlayerIdentifier;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Team {
    private final String name;
    private String displayName;
    private final int priority;
    private final Set<String> members; // almacena UUID o nickname según el modo

    public Team(String name, String displayName, int priority, Set<String> members) {
        this.name = name;
        this.displayName = displayName;
        this.priority = priority;
        this.members = members != null ? members : new HashSet<>();
    }

    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public int getPriority() { return priority; }

    /** Devuelve los identificadores internos (UUID o nickname) */
    public Set<String> getMembers() { return members; }

    /** Devuelve los nombres visibles resueltos para mostrar al jugador */
    public Set<String> getResolvedMemberNames() {
        return members.stream()
                .map(PlayerIdentifier::resolveName)
                .collect(Collectors.toSet());
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void addMember(String identifier) {
        members.add(identifier);
    }

    public void removeMember(String identifier) {
        members.remove(identifier);
    }

    public boolean hasMember(String identifier) {
        return members.contains(identifier);
    }
}