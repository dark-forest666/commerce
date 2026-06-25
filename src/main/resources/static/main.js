document.addEventListener('DOMContentLoaded', function() {
    initCartQuantityControls();
    initCartSelection();
    initProductQuantityControls();
    initCheckoutAddressSelection();
});

function initCartQuantityControls() {
    document.querySelectorAll('.quantity-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const input = this.parentElement.querySelector('input[type="number"]');
            const step = parseInt(input.step) || 1;
            const min = parseInt(input.min) || 1;
            const max = parseInt(input.max) || 999;
            
            if (this.dataset.action === 'decrease') {
                input.value = Math.max(min, parseInt(input.value) - step);
            } else {
                input.value = Math.min(max, parseInt(input.value) + step);
            }
            
            const form = input.closest('form');
            if (form) {
                form.submit();
            }
        });
    });
}

function initCartSelection() {
    const selectAllCheckbox = document.getElementById('selectAll');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function() {
            document.querySelectorAll('.cart-item-select').forEach(checkbox => {
                checkbox.checked = this.checked;
            });
            updateSelectedTotal();
        });
    }
    
    document.querySelectorAll('.cart-item-select').forEach(checkbox => {
        checkbox.addEventListener('change', updateSelectedTotal);
    });
    
    document.getElementById('selectAllBtn')?.addEventListener('click', function() {
        document.querySelectorAll('.cart-item-select').forEach(checkbox => {
            checkbox.checked = true;
        });
        if (selectAllCheckbox) selectAllCheckbox.checked = true;
        updateSelectedTotal();
    });
    
    document.getElementById('unselectAllBtn')?.addEventListener('click', function() {
        document.querySelectorAll('.cart-item-select').forEach(checkbox => {
            checkbox.checked = false;
        });
        if (selectAllCheckbox) selectAllCheckbox.checked = false;
        updateSelectedTotal();
    });
}

function updateSelectedTotal() {
    const selectedItems = document.querySelectorAll('.cart-item-select:checked');
    let total = 0;
    
    selectedItems.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const subtotal = row.querySelector('.subtotal');
        if (subtotal) {
            const price = parseFloat(subtotal.textContent.replace('￥', ''));
            total += price;
        }
    });
    
    const totalSpan = document.getElementById('totalPriceSpan');
    if (totalSpan) {
        totalSpan.textContent = '￥' + total.toFixed(2);
    }
}

function initProductQuantityControls() {
    document.querySelectorAll('.product-quantity-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const input = this.parentElement.querySelector('input[type="number"]');
            const step = parseInt(input.step) || 1;
            const min = parseInt(input.min) || 1;
            
            if (this.dataset.action === 'decrease') {
                input.value = Math.max(min, parseInt(input.value) - step);
            } else {
                input.value = parseInt(input.value) + step;
            }
        });
    });
}

function initCheckoutAddressSelection() {
    document.querySelectorAll('input[name="addressId"]').forEach(radio => {
        radio.addEventListener('change', function() {
            document.querySelectorAll('input[name="addressId"]').forEach(r => {
                const card = r.closest('div');
                if (card) {
                    card.style.borderColor = r.checked ? 'var(--primary)' : 'var(--gray-100)';
                    card.style.backgroundColor = r.checked ? 'rgba(79,70,229,0.04)' : '#fff';
                }
            });
        });
    });
}