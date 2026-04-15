package data;

import gameobjects.Player;
import java.util.ArrayList;

public class CharacterShop {
    private ArrayList<Player> unlockedChars;
    private int playerCoins;

    public CharacterShop(){
        unlockedChars = new ArrayList<>();
    }
    

    public boolean isUnlocked(Player character){
        if (unlockedChars.contains(character)){
            return true;
        } 
        return false;
    }

    public ArrayList getAvailableChars(){
        return unlockedChars;
    }
}
