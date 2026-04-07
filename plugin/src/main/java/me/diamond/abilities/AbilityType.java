package me.diamond.abilities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AbilityType {
    INFERNO(Inferno.class),
    SORCERER(Sorcerer.class),
    HACKER(Hacker.class),
    AQUA_MAN(AquaMan.class);

    private final Class<? extends Ability> clazz;
}