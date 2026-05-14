package com.ssafy.home.house;

import java.util.List;

public record HouseDetailDto(HouseInfoDto house, List<HouseDealDto> deals) {
}
