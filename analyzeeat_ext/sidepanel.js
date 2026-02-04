window.onload = () => {
    document.getElementById('nutritionBtn')
        .addEventListener('click', () => analyzeIngredients());

    document.getElementById('swapBtn')
        .addEventListener('click', () => analyzeIngredients('HEALTHY_SWAP'));
};


async function analyzeIngredients(operation = 'NUTRITION') {
    try{
        
        const [tab] = await chrome.tabs.query({active: true, currentWindow: true})
        const [{result}] = await chrome.scripting.executeScript({
            target: {tabId: tab.id},
            func: () => window.getSelection().toString()
        });
        if(!result || result.trim() === ""){
            showResult('יש לבחור את רשימת המצרכים');
            return;
        }

        const response = await fetch('http://localhost:8080/api/nutrition/analyze', {
            method: 'POST',
            headers: { 'content-Type' : 'application/json'},
            body: JSON.stringify({ content: result , operation })
        });

        if(!response.ok){
            throw new Error(`API Error: ${response.status}`);
        }
        const data = await response.json();
        const formattedText =
            operation === 'HEALTHY_SWAP' ? renderSwapResult(data) : renderNutritionResult(data);

showResult(formattedText);
        showResult(formattedText);

    }catch(error){
        showResult('Error: ' + error.message)
    }

}

function showResult(content){
    document.getElementById('result').innerHTML = 
    `<div class="result-item"><div class="result-content">${content}</div></div>`
}

function renderNutritionResult(data) {
    return `
        <div class="card">
            <div class="card-title">🥗 ניתוח תזונתי למנה</div>

            <div class="ingredients">
                <div class="section-title"> רכיבים שזוהו במתכון</div>
                <div class="ingredients-list">${data.detected_ingredients}</div>
            </div>

            <div class="stats-grid">
                <div class="stat">
                    <div class="stat-value">${data.calories_per_serving}</div>
                    <div class="stat-label"> 🔥 קלוריות</div>
                </div>

                <div class="stat">
                    <div class="stat-value">${data.protein_per_serving}g</div>
                    <div class="stat-label">💪 חלבון</div>
                </div>

                <div class="stat">
                    <div class="stat-value">${data.fat_per_serving}g</div>
                    <div class="stat-label">🧈 שומן</div>
                </div>

                <div class="stat">
                    <div class="stat-value">${data.carbs_per_serving}g</div>
                    <div class="stat-label">🍞 פחמימות</div>
                </div>

                <div class="stat servings">
                    <div class="stat-value">${data.servings}</div>
                    <div class="stat-label">🥣 מנות במתכון</div>
                </div>
            </div>
        </div>
    `;
}


function renderSwapResult(data) {
    const swapsHtml = data.swaps.map(swap => `
        <div class="swap-stat">
            <div class="swap-name">❌ ${swap.original} ← ✅ ${swap.replacement}</div>
            <div class="swap-benefit">${swap.benefit}</div>
            <div class="swap-calories">🔥 חיסכון קלורי: ${swap.calories_saved} קלוריות</div>
        </div>
    `).join('');

    return `
        <div class="card">
            <div class="card-title">🥦 הצעות חלופיות</div>

            <div class="ingredients">
                <div class="section-title">רכיבים שזוהו</div>
                <div class="ingredients-list">${data.detected_ingredients}</div>
            </div>

            <div class="swaps-grid">
                ${swapsHtml}
            </div>
        </div>
    `;
}

