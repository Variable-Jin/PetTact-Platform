<template>
    <div class="card mt-4">
        <div class="card-body">
            <h5 class="card-title">최근 삭제된 게시물</h5>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>게시물 번호</th>
                        <th>제목</th>
                        <th>카테고리</th>
                        <th>작성자</th>
                        <th>삭제일</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="board in boards" :key="board.boardNo">
                        <td>{{ board.boardNo }}</td>
                        <td>{{ board.boardTitle }}</td>
                        <td>{{ board.categoryName }}</td>
                        <td>{{ board.userNickname }}</td>
                        <td>{{ board.deletedAt }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const boards = ref([]);

onMounted(async () => {
    try {
        const res = await axios.get('/v1/admin/dashboard/board/recent-deleted', {
            params: {
                todayOnly: true,
                limit: 10
            }
        });
        console.log("res : " , res)

    } catch (e) {
        console.error('[최근 삭제된 게시물 API 오류]', e);
    }
});
</script>
