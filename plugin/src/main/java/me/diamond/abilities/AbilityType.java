package me.diamond.abilities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AbilityType {
    INFERNO(Inferno.class, "Master the art of fire"),
    SORCERER(Sorcerer.class, "Perform magic tricks and cast spells on others"),
    HACKER(Hacker.class, "A legal cheat"),
    AQUAMAN(AquaMan.class, "Become the master of the seas");

    private final Class<? extends Ability> clazz;
    private final String description;
}