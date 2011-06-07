Reagent - A Minecraft/Bukkit plugin.
====================================

A reagent is a "substance or compound that is added to a system in order to bring about a chemical reaction or is added to see if a reaction occurs."

Reagent is a plugin for CraftBukkit servers that adds the ability for players to cast magic spells. What makes Reagent unique is that when a player casts a spell a configurable amount of materials are consumed. Of course, if the server administrator wishes they could make all spells free but I think that defeats the purpose.

I wanted to create a magic system that had capability to restrict the usage of the spells as to not totally destroy the "Minecraftness" of the game. IMHO, making spells expensive to use makes them more interesting, makes them used less and keeps the immersion of Minecraft by using mined materials as a sort of "cost" to cast the spells.

When using Reagent, players need to pay more attention to when they should cast a spell since the cost of casting could be quite substantial depending on how the server is configured.


Change Log
----------

v0.4

- This is kind of experimental. You can now cast using an item!

v0.3

- Fixed bug where sometimes the materials would not be consumed if you had multiple small stacks of the same material.
- Removed the air spell. (Never worked)
- Added 4 new spells.

v0.2

- Added a cooldown timer for each spell.
- Configuration was changed to allow for cooldown.
- If permissions is not installed then all players will pay for spells (Ops included).

v0.1

- Initial release.


Known Issues
------------

- Cannot target far away blocks when empty handed. (Bukkit bug?)


Requirements
------------

- You must be using at least build 733 of CraftBukkit.
- You need permissions to be able to give users the ability to cast spells. Without Permissions only OPS can cast spells.


TODO
----

- Add more spells!