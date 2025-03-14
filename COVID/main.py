def should_mandate_vaccine(
    efficacy: float,
    voluntary_uptake:  float,
    mandated_uptake: float,
    health_utility: float,
    freedom_loss: float,
    enforcement_cost: float,
):
    # Calculate benefits
    voluntary_benefit = efficacy * voluntary_uptake * health_utility
    mandated_benefit = efficacy * mandated_uptake * health_utility

    # Calculate costs
    mandate_cost = freedom_loss * mandated_uptake + enforcement_cost

    # Compare
    return (mandated_benefit - voluntary_benefit) > mandate_cost


if __name__ == "__main__":
    # Example
    efficacy = 0.9  # 90% effective
    voluntary_uptake = 0.6  # 60% get it voluntarily
    mandated_uptake = 0.9  # 90% get it with mandate
    health_utility = 100  # Utility per 1% vaccinated
    freedom_loss = 30  # Utility loss per 1% mandated
    enforcement_cost = 500  # Fixed cost of enforcing the mandate

    decision = should_mandate_vaccine(
        efficacy,
        voluntary_uptake,
        mandated_uptake,
        health_utility,
        freedom_loss,
        enforcement_cost,
    )

    print("Mandate recommended:", decision)
