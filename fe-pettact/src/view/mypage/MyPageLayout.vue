<template>
    <div>
        <h2>My page</h2>
        <div style="display: flex;">

            <div>
                <p>사용자 닉네임</p>

                <ul>
                    <li><router-link :to="{ name: 'myInfo' }">나의 정보</router-link></li>
                    <li><router-link :to="{ name: 'myInfo' }">나의 반려동물</router-link></li>
                    <li><router-link :to="{ name: 'myInfo' }">나의 관심공고</router-link></li>
                    <li><router-link :to="{ name: 'myInfo' }">일기</router-link></li>
                    <li><router-link :to="{ name: 'myMarket' }">나의 마켓</router-link></li>
                    <li><router-link :to="{ name: 'myActivity' }">나의 활동</router-link></li>
                </ul>
            </div>

            <div>
                <router-view />
            </div>
        </div>
    </div>
</template>