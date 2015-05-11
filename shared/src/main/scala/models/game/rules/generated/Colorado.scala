// Generated rules for Solitaire.gg.
package models.game.rules.generated

import models.game._
import models.game.rules._

/**
 * Original Settings:
 *   Foundation name (F0Nm): Aces Foundation
 *   Foundation initial cards (F0d): 0 (None)
 *   Number of foundation piles (F0n): 4 (4 stacks)
 *   TODO (F0u): 2
 *   Foundation name (F1Nm): Kings Foundation
 *   Foundation low rank (F1b): 22 (Deck's high card)
 *   Foundation initial cards (F1d): 0 (None)
 *   Number of foundation piles (F1n): 4 (4 stacks)
 *   Foundation rank match rule (F1r): 32 (Build down)
 *   TODO (F1u): 2
 *   Foundation Sets (Fn): 2
 *   Auto-fill an empty tableau from (T0af): 4 (Stock)
 *   Tableau initial cards (T0d): 1 (1 card)
 *   Empty tableau is filled with (T0f): 5 (No card)
 *   Tableau piles (T0n): 20
 *   May move to non-empty tableau from (T0o): 1 (Stock)
 *   Tableau rank match rule for building (T0r): 8191 (Regardless of rank)
 *   Tableau suit match rule for building (T0s): 5 (Regardless of suit)
 *   Number of waste piles (W0n): 0
 *   Deal cards from stock (dealto): 7 (Manually)
 *   Similar to (like): twenty
 *   Number of decks (ndecks): 2 (2 decks)
 */
object Colorado extends GameRules(
  id = "colorado",
  title = "Colorado",
  like = Some("twenty"),
  links = Seq(
    Link("Wikipedia", "en.wikipedia.org/wiki/Colorado_(game)"),
    Link("Pretty Good Solitaire", "www.goodsol.com/pgshelp/colorado.htm"),
    Link("Xolitaire", "www.escapedivision.com/xolitaire/en/games/colorado.html"),
    Link("Solsuite Solitaire", "www.solsuite.com/games/colorado.htm"),
    Link("Solavant Solitaire", "www.solavant.com/solitaire/colorado.php"),
    Link("dogMelon", "www.dogmelon.com.au/solhelp/Colorado.shtml"),
    Link("BVS Solitaire Collection", "www.bvssolitaire.com/rules/colorado.htm")
  ),
  description = "A game where cards may be stacked arbitrarily on 20 tableau piles. Usually winnable, but requires some planning.",
  deckOptions = DeckOptions(
    numDecks = 2
  ),
  stock = Some(
    StockRules(
      dealTo = StockDealTo.Manually,
      maximumDeals = Some(1)
    )
  ),
  foundations = Seq(
    FoundationRules(
      name = "Aces Foundation",
      numPiles = 4,
      initialCardRestriction = Some(FoundationInitialCardRestriction.UniqueSuits),
      wrapFromKingToAce = true,
      autoMoveCards = true
    ),
    FoundationRules(
      name = "Kings Foundation",
      setNumber = 1,
      numPiles = 4,
      lowRank = FoundationLowRank.DeckHighRank,
      initialCardRestriction = Some(FoundationInitialCardRestriction.UniqueSuits),
      rankMatchRule = RankMatchRule.Down,
      wrapFromKingToAce = true,
      autoMoveCards = true
    )
  ),
  tableaus = Seq(
    TableauRules(
      numPiles = 20,
      initialCards = InitialCards.Count(1),
      cardsFaceDown = TableauFaceDownCards.Count(0),
      suitMatchRuleForBuilding = SuitMatchRule.Any,
      rankMatchRuleForBuilding = RankMatchRule.Any,
      suitMatchRuleForMovingStacks = SuitMatchRule.None,
      autoFillEmptyFrom = TableauAutoFillEmptyFrom.Stock,
      emptyFilledWith = FillEmptyWith.None,
      mayMoveToNonEmptyFrom = Seq("Stock")
    )
  )
)
