package inf112.skeleton.app;

import inf112.skeleton.app.cards.IProgramCard;
import inf112.skeleton.app.cards.MoveForwardCard;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void placingACardFromHandSlot0ToProgrammingSlot0() {
        Player testPlayer = new Player();
        testPlayer.setCardinHand(0, new MoveForwardCard());
        IProgramCard testCard = testPlayer.getCardHand()[0];
        testPlayer.placeCardFromHandToSlot(0, 0);

        assertEquals(testPlayer.getProgrammingSlots()[0], testCard);
    }


}