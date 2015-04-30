package models.game.rules.custom

import models.game.rules._

object Nestor extends GameRules(
  id = "nestor",
  title = "Nestor",
  description = """
    Discard any pair of cards of the same rank, regardless of suit (for example, two Aces, two Fives, etc.).
    Only the top cards are available for play. Spaces can't be filled.
  """,
  cardRemovalMethod = CardRemovalMethod.RemovePairsOfSameRank,

  foundations = Seq(
    FoundationRules(
      numPiles = 1,
      visible = false
    )
  ),
  tableaus = Seq(
    TableauRules(
      numPiles = 8,
      initialCards = InitialCards.Count(6),
      cardsFaceDown = TableauFaceDownCards.Count(0),
      suitMatchRuleForBuilding = SuitMatchRule.None,
      rankMatchRuleForBuilding = RankMatchRule.None,
      suitMatchRuleForMovingStacks = SuitMatchRule.None,
      rankMatchRuleForMovingStacks = RankMatchRule.None
    )
  ),
  reserves = Some(
    ReserveRules(
      numPiles = 4
    )
  )
)