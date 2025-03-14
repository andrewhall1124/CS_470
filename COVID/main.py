import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

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

def plot_vaccine_mandate_heatmap(
    voluntary_uptake: float,
    mandated_uptake: float,
    freedom_loss: float,
    enforcement_cost: float,
):
    efficacy_values = np.linspace(1, 0, 50)  # Range from 0% to 100% efficacy
    health_utility_values = np.linspace(0, 5000, 50)  # Utility range
    
    decision_matrix = np.zeros((len(efficacy_values), len(health_utility_values)))
    
    for i, efficacy in enumerate(efficacy_values):
        for j, health_utility in enumerate(health_utility_values):
            decision = should_mandate_vaccine(
                efficacy,
                voluntary_uptake,
                mandated_uptake,
                health_utility,
                freedom_loss,
                enforcement_cost,
            )

            decision_matrix[i, j] = int(decision)

    plt.figure(figsize=(10, 6))
    ax = sns.heatmap(
        decision_matrix, 
        cmap="coolwarm", 
        xticklabels=10,
        yticklabels=10
        # cbar=True, 
        # cbar_kws={'label': 'Mandate Recommended (1 = Yes, 0 = No)'}
    )

        # Manually set tick locations and labels
    x_ticks = np.linspace(0, len(health_utility_values) - 1, 10, dtype=int)
    y_ticks = np.linspace(0, len(efficacy_values) - 1, 10, dtype=int)
    
    ax.set_xticks(x_ticks)
    ax.set_xticklabels([f"{int(health_utility_values[i])}" for i in x_ticks])

    ax.set_yticks(y_ticks)
    ax.set_yticklabels([f"{efficacy_values[i]:.2f}" for i in y_ticks])

    plt.xlabel("Health Utility")
    plt.ylabel("Vaccine Efficacy")
    plt.title("Decision to Mandate Vaccine Based on Efficacy and Health Utility")
    plt.show()


if __name__ == "__main__":
    # Example
    efficacy = 0.9  # 90% effective
    voluntary_uptake = 0.6  # 60% get it voluntarily
    mandated_uptake = 0.9  # 90% get it with mandate
    health_utility = 500  # Utility per 1% vaccinated
    freedom_loss = 30  # Utility loss per 1% mandated
    enforcement_cost = 200  # Fixed cost of enforcing the mandate

    decision = should_mandate_vaccine(
        efficacy,
        voluntary_uptake,
        mandated_uptake,
        health_utility,
        freedom_loss,
        enforcement_cost,
    )

    print("Mandate recommended:", decision)

    plot_vaccine_mandate_heatmap(
        voluntary_uptake=voluntary_uptake,
        mandated_uptake=mandated_uptake,
        freedom_loss=freedom_loss,
        enforcement_cost=enforcement_cost,
    )
