// move-box move
const slideHeight = 80;
const box = document.querySelector('.move-box');
const slides = document.querySelectorAll('.move-box li');
const slider = document.querySelector('.slider');
let startY = 0;
let threshold = 0;
let isClicked = false;
let currentIdx = 0;

slider.addEventListener('mousedown', (e) => {
    isClicked = true;
    slider.style.cursor = 'grab';
    // drag start point
    startY = e.pageY;
    console.log('mousedown');
})

slider.addEventListener('mouseup', (e) => mouseOff(e));
slider.addEventListener('mouseleave', (e) => mouseOff(e));

slider.addEventListener('mousemove', (e) => {
    e.preventDefault();
    let moveY = e.pageY;

    if(isClicked) {
    console.log('mousemove');
        threshold += (moveY - startY);
        startY = moveY;

        if(threshold <= -80 && (currentIdx < (slides.length - slider.offsetHeight / slideHeight) )){
        console.log('currentIdx++');
            currentIdx++;
            resetDrag();
            //console.log('currentIdx: ' + currentIdx);
        } else if (threshold >= 80 && currentIdx > 0) {
        console.log('currentIdx--');
            currentIdx--;
            resetDrag();
            //console.log('currentIdx: ' + currentIdx);
        }
    }

})

function moveSlide(index){
    let offset = 0;
    offset = `-${80*currentIdx}px`;
    box.style.top = offset;
}

function resetDrag(){
    threshold = 0;
    isClicked = false; // 한 번 드래그할 때 한 슬라이드씩만
}

function mouseOff(e){
    e.preventDefault();
    moveSlide(currentIdx);
    resetDrag();
}
