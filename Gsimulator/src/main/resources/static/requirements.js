//졸업학점 관리(학과별)
const graduationRequirements = {
  '컴퓨터공학부': {
    '2020': { 총학점: 130, 교양: 39, 교양필수: 24, 교양선택: 15, 전공: 75, 채플: 7},
    '2021': { 총학점: 130, 교양: 39, 교양필수: 24, 교양선택: 15, 전공: 75, 채플: 7},
    '2022': { 총학점: 140, 교양: 39, 교양필수: 24, 교양선택: 15, 전공: 85, 채플: 7},
  }
};

// 학과와 입학년도에 따라 졸업 요건을 가져오는 함수
function getGraduationRequirements(major, enteryear) {
  return graduationRequirements[major] && graduationRequirements[major][enteryear];
}

// 선택영역 관리(년도별)
const caltureRequirements = {
  '2020': { cs1: '인성 교양', cs2: '인문학', cs3: '사회 과학', cs4: '자연 과학', cs5: '문화 예술', cs6: '인문 예술'},
  '2022': { cs1: '인성 교양', cs2: '디지털리터러시', cs3: '사회 과학', cs4: '자연 과학', cs5: '문화 예술', cs6: '인문 예술'}
};

// 입학년도에 따라 졸업 요건을 가져오는 함수
function getCaltureRequirements(enteryear) {
  return caltureRequirements[enteryear];
}

